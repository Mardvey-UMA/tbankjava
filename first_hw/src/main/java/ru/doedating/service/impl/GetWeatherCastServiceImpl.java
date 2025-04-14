package ru.doedating.service.impl;

import lombok.RequiredArgsConstructor;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.service.interfaces.GenerateRandomCast;
import ru.doedating.service.interfaces.GetWeatherCastService;

import java.util.HashMap;

@RequiredArgsConstructor
public class GetWeatherCastServiceImpl implements GetWeatherCastService {

    private final GenerateRandomCast generator;

    @Override
    public String generateRandomWeatherCast(String city, HashMap<String, String> cashedWeatherCast) throws EmptyCityException, InvalidCityException {

        city = city.trim();

        if (city.isBlank()) {
            throw new EmptyCityException();
        }

        if (!city.matches("[a-zA-Zа-яА-ЯёЁ]+")){
            throw new InvalidCityException();
        }

        if (cashedWeatherCast.containsKey(city)) {
            return cashedWeatherCast.get(city);
        }

        String wCast = generator.generateRandomCastByCity(city);
        cashedWeatherCast.put(city, wCast);
        return wCast;

    }
}
