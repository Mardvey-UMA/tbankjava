package ru.doedating.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.doedating.dto.CastGeneratorDTO;
import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.mapper.WeatherCastMapper;
import ru.doedating.repository.CastCityRepository;
import ru.doedating.repository.CityRepository;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.CoordinatesService;
import ru.doedating.service.interfaces.WeatherCastService;
import ru.doedating.repository.WeatherCastRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherCastServiceImpl implements WeatherCastService {

    private final CastGeneratorService generator;
    private final CoordinatesService coordinatesService;
    private final WeatherCastRepository weatherCastRepository;
    private final CityRepository cityRepository;
    private final CastCityRepository castCityRepository;
    @Autowired
    private final WeatherCastMapper mapper;

    @Override
    public WeatherCastDTO getWeatherCastByCityAndDate(String city, String date) throws EmptyCityException, InvalidCityException {

        if (city == null || city.isBlank()) {
            throw new EmptyCityException();
        }

        CityEntity cityEntity = cityRepository.findByName(city);

        if (cityEntity == null) {
            List<BigDecimal> coords = coordinatesService.getCoordinatesByCityName(city);
            if (coords == null) {
                throw new InvalidCityException();
            }
            cityEntity = new CityEntity(city, coords.get(0), coords.get(1));
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

        return mapper.toDto(cityEntity, weatherCast, date);
    }
}