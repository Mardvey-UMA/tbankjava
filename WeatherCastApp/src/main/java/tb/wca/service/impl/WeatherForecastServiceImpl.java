package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.client.WeatherApiClient;
import tb.wca.dto.DateCoordsCityDTO;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.exceptions.InvalidCityException;
import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.exceptions.InvalidHourFormatException;
import tb.wca.mapper.WeatherMapper;
import tb.wca.model.CityGeoModel;
import tb.wca.model.WeatherModel;
import tb.wca.repository.CityRepository;
import tb.wca.repository.CityWeatherRepository;
import tb.wca.repository.WeatherCastRepository;
import tb.wca.service.interfaces.CoordinatesService;
import tb.wca.service.interfaces.WeatherForecastService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private final CoordinatesService coordinatesService;
    private final WeatherApiClient weatherApiClient;

    private final WeatherCastRepository weatherCastRepository;
    private final CityWeatherRepository cityWeatherRepository;
    private final CityRepository cityRepository;

    private final WeatherMapper weatherMapper;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static final Integer HOURS_IN_DAY = 24;
    private static final String MIDNIGHT = "00";
    private static final String COMMA = ",";
    private static final String CORRECT_HOUR_REGEX = "^(0[0-9]|1[0-9]|2[0-3])$";
    private static final Set<String> MILLION_CITIES = Set.of(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань",
            "Нижний Новгород", "Челябинск", "Самара", "Омск", "Ростов-на-Дону",
            "Уфа", "Красноярск", "Воронеж", "Пермь", "Волгоград", "Саратов");

    @Override
    public List<WeatherModel> getWeatherForecastByDay(String cityName, String day) {

        DateCoordsCityDTO weatherInfo = getRequestDateAndCityCoords(
                cityName,
                day);

        List<CityWeatherEntity> existingForecasts = cityWeatherRepository.findByCity_NameAndDate(
                cityName,
                weatherInfo.requestDate());

        if (existingForecasts.size() >= HOURS_IN_DAY) return mapCityWeatherToModels(existingForecasts);

        List<WeatherModel> apiForecasts = getWeather(
                weatherInfo.coordinates(),
                day);

        var existingHours = existingForecasts.stream()
                .map(CityWeatherEntity::getTime)
                .collect(Collectors.toSet());

        List<WeatherModel> missingForecasts = apiForecasts.stream()
                .filter(wm -> !existingHours.contains(wm.time()))
                .toList();

        if (!missingForecasts.isEmpty()) saveWeatherModelsForCityAndDate(
                missingForecasts,
                weatherInfo.cityEntity(),
                weatherInfo.requestDate());

        return mapCityWeatherToModels(cityWeatherRepository.findByCity_NameAndDate(
                cityName,
                weatherInfo.requestDate()));
    }

    @Override
    public List<WeatherModel> getWeatherForecastByDayAndHour(String cityName, String day, String hour) {

        DateCoordsCityDTO weatherInfo = getRequestDateAndCityCoords(cityName, day, hour);

        if (MILLION_CITIES.contains(weatherInfo.cityEntity().getName())){
            List<WeatherModel> apiForecasts = getWeather(
                    weatherInfo.coordinates(),
                    day);

            saveWeatherModelsForCityAndDate(
                    apiForecasts,
                    weatherInfo.cityEntity(),
                    weatherInfo.requestDate());
        }

        Optional<CityWeatherEntity> existingForecastOpt = cityWeatherRepository.
                findByCity_NameAndDateAndTime(
                        cityName, weatherInfo.requestDate(),
                        weatherInfo.requestTime());

        if (existingForecastOpt.isPresent()) return List.of(
                weatherMapper.cityWeatherEntityToWeatherModel(
                        existingForecastOpt.get()));

        String requestDateTime = LocalDateTime.of(weatherInfo.requestDate(),
                weatherInfo.requestTime()).format(dateTimeFormatter);

        List<WeatherModel> apiForecast = getWeather(
                weatherInfo.coordinates(),
                requestDateTime);

        saveWeatherModelsForCityAndDate(apiForecast, weatherInfo.cityEntity(), weatherInfo.requestDate());

        return apiForecast;
    }

    @Override
    public List<WeatherModel> getWeatherForecastByRange(String cityName, String startDate, String endDate) {

        DateCoordsCityDTO weatherInfo = getDateCityCoordsByRange(cityName, startDate, endDate);

        List<CityWeatherEntity> existingForecasts = cityWeatherRepository.findByCity_NameAndDateBetween(cityName, weatherInfo.startDate(), weatherInfo.endDate());
        long totalExpectedHours = (weatherInfo.endDate().toEpochDay() - weatherInfo.startDate().toEpochDay() + 1) * HOURS_IN_DAY;

        if (existingForecasts.size() >= totalExpectedHours) return mapCityWeatherToModels(existingForecasts);

        String requestRange = weatherInfo.startDate() + COMMA + weatherInfo.endDate();

        List<WeatherModel> apiForecasts = getWeather(weatherInfo.coordinates(), requestRange);

        Set<LocalDateTime> existingDateTimeSet = existingForecasts.stream()
                .map(cw -> cw.getDate().atTime(cw.getTime()))
                .collect(Collectors.toSet());

        List<WeatherModel> toSave = apiForecasts.stream()
                .filter(wm -> !existingDateTimeSet.contains(LocalDateTime.of(wm.date(), wm.time())))
                .toList();

        if (!toSave.isEmpty()) {
            Map<LocalDate, List<WeatherModel>> groupedByDate = toSave.stream()
                    .collect(Collectors.groupingBy(WeatherModel::date));

            for (var entry : groupedByDate.entrySet()) {
                saveWeatherModelsForCityAndDate(entry.getValue(), weatherInfo.cityEntity(), entry.getKey());
            }
        }

        List<CityWeatherEntity> updatedForecasts = cityWeatherRepository.findByCity_NameAndDateBetween(cityName, weatherInfo.startDate(), weatherInfo.endDate());

        return mapCityWeatherToModels(updatedForecasts);
    }

    private DateCoordsCityDTO getRequestDateAndCityCoords(String cityName, String date) {
        LocalDate requestDate = parseDateOrThrow(date);
        CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);
        CityEntity city = getCityOrThrow(cityName);
        return DateCoordsCityDTO.builder()
                .coordinates(coordinates)
                .cityEntity(city)
                .requestDate(requestDate)
                .build();
    }

    private DateCoordsCityDTO getRequestDateAndCityCoords(String cityName, String date, String hour) {
        DateCoordsCityDTO correctDateAndCoordinates = getRequestDateAndCityCoords(cityName, date);
        LocalTime requestTime =  checkHourFormat(hour);
        return DateCoordsCityDTO.builder()
                .coordinates(correctDateAndCoordinates.coordinates())
                .cityEntity(correctDateAndCoordinates.cityEntity())
                .requestDate(correctDateAndCoordinates.requestDate())
                .requestTime(requestTime)
                .build();
    }

    private DateCoordsCityDTO getDateCityCoordsByRange(String cityName, String startDate, String endDate) {
        LocalDate requestEndDate = parseDateOrThrow(endDate);
        DateCoordsCityDTO correctDateAndCoordinates = getRequestDateAndCityCoords(cityName, startDate);
        if (correctDateAndCoordinates.startDate().isBefore(requestEndDate)) throw new InvalidDateFormatException();
        return DateCoordsCityDTO.builder()
                .coordinates(correctDateAndCoordinates.coordinates())
                .cityEntity(correctDateAndCoordinates.cityEntity())
                .startDate(correctDateAndCoordinates.requestDate())
                .endDate(requestEndDate)
                .build();
    }

    private List<WeatherModel> getWeather(CityGeoModel coordinates, String requestParam) {
        return weatherApiClient.getWeather(
                coordinates.lat().doubleValue(),
                coordinates.lon().doubleValue(),
                requestParam
        );
    }

    private LocalTime checkHourFormat(String hour) {
        if (HOURS_IN_DAY.toString().equals(hour)) {
            hour = MIDNIGHT;
        }
        if (!hour.matches(CORRECT_HOUR_REGEX)) {
            throw new InvalidHourFormatException();
        }
        return LocalTime.of(Integer.parseInt(hour), 0);
    }

    private LocalDate parseDateOrThrow(String dateStr) {
        try {
            return LocalDate.parse(dateStr, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }
    }

    private CityEntity getCityOrThrow(String cityName) {
        return cityRepository.findByName(cityName).orElseThrow(InvalidCityException::new);
    }

    private List<WeatherModel> mapCityWeatherToModels(List<CityWeatherEntity> cityWeatherList) {
        return cityWeatherList.stream()
                .map(weatherMapper::cityWeatherEntityToWeatherModel)
                .toList();
    }

    private void saveWeatherModelsForCityAndDate(List<WeatherModel> weatherModels, CityEntity city, LocalDate date) {
        List<WeatherCastEntity> entities = weatherMapper.modelToEntity(weatherModels);
        List<WeatherCastEntity> savedEntities = weatherCastRepository.saveAll(entities);

        AtomicInteger index = new AtomicInteger();
        List<CityWeatherEntity> cityWeatherEntities = weatherModels.stream()
                .map(wm -> {
                    WeatherCastEntity weatherEntity = savedEntities.get(index.getAndIncrement());
                    return CityWeatherEntity.builder()
                            .city(city)
                            .weather(weatherEntity)
                            .date(date)
                            .time(wm.time())
                            .build();
                })
                .toList();

        cityWeatherRepository.saveAll(cityWeatherEntities);
    }
}
