package ru.doedating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.doedating.entity.WeatherCastEntity;

import java.util.List;

public interface WeatherCastRepository extends JpaRepository<WeatherCastEntity, Long> {
}
