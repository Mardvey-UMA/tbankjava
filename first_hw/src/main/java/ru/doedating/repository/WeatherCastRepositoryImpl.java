package ru.doedating.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.mapper.WeatherCastRowMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class WeatherCastRepositoryImpl implements WeatherCastRepository {

    private final JdbcTemplate jdbcTemplate;
    private final WeatherCastRowMapper mapper;

    @Override
    public WeatherCastEntity save(WeatherCastEntity w) {
        final String sql = """
            INSERT INTO weather (temperature, humidity, wind_speed)
            VALUES (?,?,?)
            RETURNING id, temperature, humidity, wind_speed
        """;

        return jdbcTemplate.queryForObject(sql, mapper,
                w.getTemperature(),
                w.getHumidity(),
                w.getWindSpeed());
    }

    @Override
    public WeatherCastEntity findByCityAndDate(String city, String date) {
        final String sql = """
            SELECT w.id, w.temperature, w.humidity, w.wind_speed
            FROM weather        w
            JOIN city_weather   cw ON w.id   = cw.weather_id
            JOIN city           c  ON cw.city_id = c.id
            WHERE c.name = ? AND cw.date = ?
        """;

        return DataAccessUtils.singleResult(jdbcTemplate.query(sql, mapper,
                city,
                Date.valueOf(LocalDate.parse(date))));
    }

}
