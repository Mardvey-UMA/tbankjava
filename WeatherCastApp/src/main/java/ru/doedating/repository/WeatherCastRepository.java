package ru.doedating.repository;

public interface WeatherCastRepository {
    String getWeatherCastByCity(String city);
    void saveWeatherCast(String city, String weatherCast);
    boolean containsCity(String city);
}
