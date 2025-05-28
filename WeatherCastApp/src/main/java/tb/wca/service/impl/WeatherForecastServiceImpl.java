package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.client.WeatherApiClient;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.exceptions.InvalidCityException;
import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.exceptions.InvalidHourFormatException;
import tb.wca.exceptions.NotFoundDataException;
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

    @Override
    public List<WeatherModel> getWeatherForecastByDay(String cityName, String day) {
        LocalDate requestDate = parseDateOrThrow(day);
        CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);
        CityEntity city = getCityOrThrow(cityName);
        List<CityWeatherEntity> existingForecasts = cityWeatherRepository.findByCity_NameAndDate(cityName, requestDate);

        if (existingForecasts.size() >= 24) {
            return mapCityWeatherToModels(existingForecasts);
        }

        List<WeatherModel> apiForecasts = weatherApiClient.getWeather(
                coordinates.lat().doubleValue(),
                coordinates.lon().doubleValue(),
                requestDate.toString()
        );

        var existingHours = existingForecasts.stream()
                .map(CityWeatherEntity::getTime)
                .collect(Collectors.toSet());

        List<WeatherModel> missingForecasts = apiForecasts.stream()
                .filter(wm -> !existingHours.contains(wm.time()))
                .toList();

        if (!missingForecasts.isEmpty()) {
            saveWeatherModelsForCityAndDate(missingForecasts, city, requestDate);
        }

        List<CityWeatherEntity> totalForecasts = cityWeatherRepository.findByCity_NameAndDate(cityName, requestDate);

        return mapCityWeatherToModels(totalForecasts);
    }

    @Override
    public List<WeatherModel> getWeatherForecastByDayAndHour(String cityName, String day, String hour) {
        LocalDate requestDate = parseDateOrThrow(day);
        CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);
        if ("24".equals(hour)) {
            hour = "00";
        }
        if (!hour.matches("^(0[0-9]|1[0-9]|2[0-3])$")) {
            throw new InvalidHourFormatException();
        }

        LocalTime requestTime = LocalTime.of(Integer.parseInt(hour), 0);
        CityEntity city = getCityOrThrow(cityName);

        Optional<CityWeatherEntity> existingForecastOpt =
                cityWeatherRepository.findByCity_NameAndDateAndTime(cityName, requestDate, requestTime);

        if (existingForecastOpt.isPresent()) {
            return List.of(weatherMapper.cityWeatherEntityToWeatherModel(existingForecastOpt.get()));
        }

        LocalDateTime dateTime = LocalDateTime.of(requestDate, requestTime);
        List<WeatherModel> apiForecast = weatherApiClient.getWeather(
                coordinates.lat().doubleValue(),
                coordinates.lon().doubleValue(),
                dateTime.format(dateTimeFormatter)
        );

        if (apiForecast.isEmpty()) {
            throw new NotFoundDataException();
        }

        saveWeatherModelsForCityAndDate(apiForecast, city, requestDate);

        return apiForecast;
    }

    @Override
    public List<WeatherModel> getWeatherForecastByRange(String cityName, String startDate, String endDate) {
        LocalDate localStartDate = parseDateOrThrow(startDate);
        LocalDate localEndDate = parseDateOrThrow(endDate);
        CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);

        if (localEndDate.isBefore(localStartDate)) {
            throw new InvalidDateFormatException();
        }

        CityEntity city = getCityOrThrow(cityName);

        List<CityWeatherEntity> existingForecasts = cityWeatherRepository.findByCity_NameAndDateBetween(cityName, localStartDate, localEndDate);

        long totalExpectedHours = (localEndDate.toEpochDay() - localStartDate.toEpochDay() + 1) * 24;

        if (existingForecasts.size() >= totalExpectedHours) {
            return mapCityWeatherToModels(existingForecasts);
        }

        String requestRange = localStartDate + "," + localEndDate;

        List<WeatherModel> apiForecasts = weatherApiClient.getWeather(
                coordinates.lat().doubleValue(),
                coordinates.lon().doubleValue(),
                requestRange
        );

        var existingDateTimeSet = existingForecasts.stream()
                .map(cw -> cw.getDate().atTime(cw.getTime()))
                .collect(Collectors.toSet());

        List<WeatherModel> toSave = apiForecasts.stream()
                .filter(wm -> !existingDateTimeSet.contains(LocalDateTime.of(wm.date(), wm.time())))
                .toList();

        if (!toSave.isEmpty()) {
            Map<LocalDate, List<WeatherModel>> groupedByDate = toSave.stream()
                    .collect(Collectors.groupingBy(WeatherModel::date));

            for (var entry : groupedByDate.entrySet()) {
                saveWeatherModelsForCityAndDate(entry.getValue(), city, entry.getKey());
            }
        }

        List<CityWeatherEntity> updatedForecasts = cityWeatherRepository.findByCity_NameAndDateBetween(cityName, localStartDate, localEndDate);

        return mapCityWeatherToModels(updatedForecasts);
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
