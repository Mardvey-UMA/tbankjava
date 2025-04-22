package ru.doedating.service.interfaces;

import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;

import java.util.HashMap;

public interface WeatherCastService {
    String generateRandomWeatherCast(String city) throws InvalidCityException, EmptyCityException;
}
