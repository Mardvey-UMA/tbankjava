package ru.doedating.repository;

import ru.doedating.entity.CityEntity;

public interface CityRepository {
    CityEntity save(CityEntity city);
    boolean existsByName(String name);
    CityEntity findById(Long id);
    CityEntity findByName(String name);
}
