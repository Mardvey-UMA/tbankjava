package ru.doedating.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.doedating.entity.WeatherCastEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WeatherCastRowMapper implements RowMapper<WeatherCastEntity> {
    @Override
    public WeatherCastEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new WeatherCastEntity(
                rs.getLong("id"),
                rs.getBigDecimal("temperature"),
                rs.getBigDecimal("humidity"),
                rs.getBigDecimal("wind_speed")
        );
    }
}
