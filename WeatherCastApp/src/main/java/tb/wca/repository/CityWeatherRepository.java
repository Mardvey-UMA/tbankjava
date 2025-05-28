package tb.wca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.exceptions.InvalidCityException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface CityWeatherRepository extends JpaRepository<CityWeatherEntity, Long> {
    List<CityWeatherEntity> findByCity_NameAndDate(String city, LocalDate date);
    Optional<CityWeatherEntity> findByCity_NameAndDateAndTime(String cityName, LocalDate date, LocalTime time);
    List<CityWeatherEntity> findByCity_NameAndDateBetween(String cityName, LocalDate startDate, LocalDate endDate);
}
