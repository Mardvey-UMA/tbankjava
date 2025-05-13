package ru.doedating.service.impl;

import lombok.RequiredArgsConstructor;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.WeatherCastService;
import ru.doedating.repository.WeatherCastRepository;
import java.util.HashMap;

@RequiredArgsConstructor
public class WeatherCastServiceImpl implements WeatherCastService {

    private final CastGeneratorService generator;
    private final WeatherCastRepository repository;

    @Override
    public String generateRandomWeatherCast(String city) throws EmptyCityException, InvalidCityException {

        city = city.trim();

        if (city.isBlank()) {
            throw new EmptyCityException();
        }

        if (!city.matches("[а-яА-ЯёЁ]+")){
            throw new InvalidCityException();
        }

        if (repository.containsCity(city)) {
            return repository.getWeatherCastByCity(city);
        }

        String weatherCast = generator.generateRandomCastByCity(city);
        repository.saveWeatherCast(city, weatherCast);
        return weatherCast;
    }
}
