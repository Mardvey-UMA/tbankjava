package ru.doedating.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.doedating.dto.CastGeneratorDTO;
import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.CityWeatherEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.mapper.WeatherCastMapper;
import ru.doedating.repository.CityWeatherRepository;
import ru.doedating.repository.CityRepository;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.CoordinatesService;
import ru.doedating.service.interfaces.WeatherCastService;
import ru.doedating.repository.WeatherCastRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class WeatherCastServiceImpl implements WeatherCastService {

    private final CastGeneratorService generator;
    private final CoordinatesService coordinatesService;
    private final WeatherCastRepository weatherCastRepository;
    private final CityRepository cityRepository;
    private final CityWeatherRepository cityWeatherRepository;
    @Autowired
    private final WeatherCastMapper mapper;

    @Override
    public WeatherCastDTO getWeatherCastByCityAndDate(String city, String dateStr)
            throws EmptyCityException {

        if (city == null || city.isBlank())  throw new EmptyCityException();

        LocalDate date = LocalDate.parse(dateStr);

        CityEntity cityEntity = cityRepository.findByName(city)
                .orElseGet(() -> {
                    List<BigDecimal> coords = coordinatesService.getCoordinatesByCityName(city);
                    if (coords == null) throw new RuntimeException(new InvalidCityException());
                    return cityRepository.save(
                            CityEntity.builder()
                                    .name(city)
                                    .latitude(coords.get(0))
                                    .longitude(coords.get(1))
                                    .build()
                    );
                });

        WeatherCastEntity cast = cityWeatherRepository
                .findByCity_NameAndDate(city, date)
                .map(CityWeatherEntity::getWeather)
                .orElseGet(() -> {
                    CastGeneratorDTO generated = generator.generateRandomCast();
                    WeatherCastEntity w = weatherCastRepository.save(
                            WeatherCastEntity.builder()
                                    .temperature(generated.getTemperature())
                                    .humidity(generated.getHumidity())
                                    .windSpeed(generated.getWindSpeed())
                                    .build());

                    cityWeatherRepository.save(
                            CityWeatherEntity.builder()
                                    .city(cityEntity)
                                    .weather(w)
                                    .date(date)
                                    .build());
                    return w;
                });


        return mapper.toDto(cityEntity, cast, date.toString());
    }
}