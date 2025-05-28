package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.client.WeatherApiClient;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.exceptions.InvalidHourFormatException;
import tb.wca.mapper.WeatherMapper;
import tb.wca.model.CityGeoModel;
import tb.wca.model.WeatherModel;
import tb.wca.repository.CityWeatherRepository;
import tb.wca.repository.WeatherCastRepository;
import tb.wca.service.interfaces.CoordinatesService;
import tb.wca.service.interfaces.WeatherForecastService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private final CoordinatesService coordinatesService;
    private final WeatherApiClient weatherApiClient;
    private final WeatherCastRepository weatherCastRepository;
    private final CityWeatherRepository cityWeatherRepository;
    private final WeatherMapper weatherMapper;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public List<WeatherModel> getWeatherForecastByDay(String cityName, String day) {
        LocalDate requestDate;
        try {
            requestDate = LocalDate.parse(day, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }
        // Список всех прогнозов за дату
        List<CityWeatherEntity> forecastList = cityWeatherRepository.findByCity_NameAndDate(cityName, requestDate);
        if (forecastList.isEmpty()) { // МАПИТЬ ОТДЕЛЬНО СПИСОК ПРОГНОЗОВ ПОГОДЫ ОТДЕЛЬНО СОЗДАВАТЬ СПИСОК СВЯЗЕЙ ПО ДАТАМ
            CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);
            List<WeatherModel> newForecastList = weatherApiClient.getWeather(coordinates.lat().doubleValue(), coordinates.lon().doubleValue(), requestDate.toString());
            List<WeatherCastEntity> entities = weatherMapper.modelToEntity(newForecastList);
            List<WeatherCastEntity> savedEntities = weatherCastRepository.saveAll(entities);
        }

        return null;
    }

    @Override
    public List<WeatherModel> getWeatherForecastByDayAndHour(String cityName, String day, String hour) {
        LocalDate requestDate;
        try {
            requestDate = LocalDate.parse(day, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }

        if ("24".equals(hour)) {
            hour = "00";
        }

        if (!hour.matches("^(0[0-9]|1[0-9]|2[0-3])$")) {
            throw new InvalidHourFormatException();
        }

        LocalTime localTime = LocalTime.of(Integer.parseInt(hour), 0);
        LocalDateTime localDateTime = LocalDateTime.of(requestDate, localTime);

        String dateTime = localDateTime.format(dateTimeFormatter);

        CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);

        return weatherApiClient.getWeather(coordinates.lat().doubleValue(), coordinates.lon().doubleValue(), dateTime);
    }

    @Override
    public List<WeatherModel> getWeatherForecastByRange(String cityName,  String startDate, String endDate) {
        LocalDate localDateStart;
        LocalDate localDateEnd;
        try {
            localDateStart = LocalDate.parse(startDate, dateFormatter);
            localDateEnd = LocalDate.parse(endDate, dateFormatter);
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException();
        }

        CityGeoModel coordinates = coordinatesService.getCoordinatesByCityName(cityName);
        String requestRange = localDateStart.toString() + "," + localDateEnd.toString();
        return weatherApiClient.getWeather(coordinates.lat().doubleValue(), coordinates.lon().doubleValue(), requestRange);
    }
}
