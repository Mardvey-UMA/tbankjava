package ru.doedating.service.impl;

import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.Pair;
import ru.doedating.dto.CastGeneratorDTO;
import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.mapper.DtoMapper;
import ru.doedating.repository.CastCityRepository;
import ru.doedating.repository.CityRepository;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.CoordinatesService;
import ru.doedating.service.interfaces.WeatherCastService;
import ru.doedating.repository.WeatherCastRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;

@RequiredArgsConstructor
public class WeatherCastServiceImpl implements WeatherCastService {

    private final CastGeneratorService generator;
    private final CoordinatesService coordinatesService;
    private final WeatherCastRepository weatherCastRepository;
    private final CityRepository cityRepository;
    private final CastCityRepository castCityRepository;

    @Override
    public WeatherCastDTO getWeatherCastByCityAndDate(String city, String date) throws EmptyCityException, InvalidCityException {

        if (city == null || city.isBlank()) {
            throw new EmptyCityException();
        }

        CityEntity cityEntity = cityRepository.findByName(city);

        if (cityEntity == null) {
            Pair<BigDecimal, BigDecimal> coords = coordinatesService.getCoordinatesByCityName(city);
            if (coords == null) {
                throw new InvalidCityException();
            }
            cityEntity = new CityEntity(city, coords.a, coords.b);
            cityEntity = cityRepository.save(cityEntity);
        }

        WeatherCastEntity weatherCast = weatherCastRepository.findByCityAndDate(city, date);

        if (weatherCast == null) {
            CastGeneratorDTO generatedCast = generator.generateRandomCast();
            weatherCast = new WeatherCastEntity(
                    generatedCast.getTemperature(),
                    generatedCast.getHumidity(),
                    generatedCast.getWindSpeed()
            );
            weatherCast = weatherCastRepository.save(weatherCast);
            castCityRepository.save(cityEntity, weatherCast, date);
        }

        return DtoMapper.mapToWeatherCastDTO(cityEntity, weatherCast, date);
    }
}