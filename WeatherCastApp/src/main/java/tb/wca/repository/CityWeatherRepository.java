package tb.wca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.exceptions.InvalidCityException;

import java.time.LocalDate;
import java.util.Optional;

public interface CityWeatherRepository extends JpaRepository<CityWeatherEntity, Long> {

    Optional<CityWeatherEntity> findByCity_NameAndDate(String city, LocalDate date);
}
