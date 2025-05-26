package ru.doedating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.doedating.entity.CityEntity;

import java.util.Optional;

public interface CityRepository extends JpaRepository<CityEntity, Long> {
    Optional<CityEntity> findByName(String name);
}
