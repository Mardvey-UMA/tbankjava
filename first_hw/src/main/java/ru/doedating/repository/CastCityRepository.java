package ru.doedating.repository;

import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.InvalidCityException;

public interface CastCityRepository {
    void save(CityEntity cityEntity, WeatherCastEntity weatherCastEntity, String date) throws InvalidCityException;
}
