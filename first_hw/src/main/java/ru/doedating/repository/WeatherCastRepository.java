package ru.doedating.repository;

import ru.doedating.entity.WeatherCastEntity;

import java.util.List;

public interface WeatherCastRepository {
    WeatherCastEntity save(WeatherCastEntity weatherCastEntity);
    WeatherCastEntity findByCityAndDate(String city, String date);
}
