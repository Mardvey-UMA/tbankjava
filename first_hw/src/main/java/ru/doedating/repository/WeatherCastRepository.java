package ru.doedating.repository;

import ru.doedating.entity.WeatherCastEntity;

import java.util.List;

public interface WeatherCastRepository {
    WeatherCastEntity save(WeatherCastEntity weatherCastEntity);
    boolean existByCityAndDate(String city, String date);
    WeatherCastEntity findByCityAndDate(String city, String date);
    List<WeatherCastEntity> findAllByCity(String city);
    WeatherCastEntity findById(Long id);
}
