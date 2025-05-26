package ru.doedating.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.CityWeatherEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.InvalidCityException;

import java.time.LocalDate;
import java.util.Optional;

public interface CityWeatherRepository extends JpaRepository<CityWeatherEntity, Long> {

    Optional<CityWeatherEntity> findByCity_NameAndDate(String city, LocalDate date);
}
