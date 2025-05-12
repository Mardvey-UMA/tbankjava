package ru.doedating.repository;

import ru.doedating.entity.CityEntity;

public interface CityRepository {
    CityEntity save(CityEntity city);
    CityEntity findByName(String name);
}
