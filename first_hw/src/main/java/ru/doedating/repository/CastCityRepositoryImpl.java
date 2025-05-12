package ru.doedating.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.InvalidCityException;
import java.sql.Date;
import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class CastCityRepositoryImpl implements CastCityRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void save(CityEntity city, WeatherCastEntity cast, String date)
            throws InvalidCityException {

        if (city.getId() == null || cast.getId() == null)
            throw new InvalidCityException();

        final String sql = """
                INSERT INTO city_weather (city_id, weather_id, date)
                VALUES (?,?,?)
                ON CONFLICT (city_id, date) DO UPDATE
                    SET weather_id = EXCLUDED.weather_id
            """;

        jdbcTemplate.update(
                sql,
                ps -> {
                    ps.setLong(1, city.getId());
                    ps.setLong(2, cast.getId());
                    ps.setDate(3, Date.valueOf(LocalDate.parse(date)));
                }
        );
    }
}
