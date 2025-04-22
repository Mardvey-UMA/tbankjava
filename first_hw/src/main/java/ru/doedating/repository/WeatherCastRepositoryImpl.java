package ru.doedating.repository;

import java.util.HashMap;
import java.util.Map;

public class WeatherCastRepositoryImpl implements WeatherCastRepository {
    private final Map<String, String> weatherCache = new HashMap<>();

    @Override
    public String getWeatherCastByCity(String city) {
        return weatherCache.get(city);
    }

    @Override
    public void saveWeatherCast(String city, String weatherCast) {
        weatherCache.put(city, weatherCast);
    }

    @Override
    public boolean containsCity(String city) {
        return weatherCache.containsKey(city);
    }
}
