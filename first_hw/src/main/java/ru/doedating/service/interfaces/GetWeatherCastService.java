package ru.doedating.service.interfaces;

import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;

import java.util.HashMap;

public interface GetWeatherCastService {
    String generateRandomWeatherCast(String city, HashMap<String, String> cashedWeatherCast) throws InvalidCityException, EmptyCityException;
}
