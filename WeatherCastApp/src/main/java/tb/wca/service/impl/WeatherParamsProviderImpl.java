package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.entity.CityEntity;
import tb.wca.exceptions.InvalidCityException;
import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.model.CityGeoModel;
import tb.wca.model.RequestParams;
import tb.wca.repository.CityRepository;
import tb.wca.service.interfaces.CoordinatesService;
import tb.wca.service.interfaces.WeatherParamsProvider;
import tb.wca.util.DateTimeValidator;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class WeatherParamsProviderImpl implements WeatherParamsProvider {

    private final CoordinatesService coordinatesService;
    private final CityRepository cityRepository;

    @Override
    public RequestParams byDay(String city, String day) {
        LocalDate date = DateTimeValidator.parseDate(day);
        return build(city, date, null, null);
    }

    @Override
    public RequestParams byDayHour(String city, String day, String hour) {
        LocalDate date = DateTimeValidator.parseDate(day);
        LocalTime time = DateTimeValidator.parseHour(hour);
        return build(city, date, time, null);
    }

    @Override
    public RequestParams byRange(String city, String start, String end) {
        LocalDate s = DateTimeValidator.parseDate(start);
        LocalDate e = DateTimeValidator.parseDate(end);
        if (s.isAfter(e)) throw new InvalidDateFormatException();
        return build(city, s, null, e);
    }

    private RequestParams build(String cityName, LocalDate start, LocalTime time, LocalDate end) {
        CityGeoModel coords = coordinatesService.getCoordinatesByCityName(cityName);
        CityEntity city = cityRepository.findByName(cityName)
                .orElseThrow(InvalidCityException::new);
        return new RequestParams(city, coords, start, time, end);
    }
}
