package ru.doedating.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.doedating.entity.CityEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CityRowMapper implements RowMapper<CityEntity> {
    @Override
    public CityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CityEntity(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getBigDecimal("latitude"),
                rs.getBigDecimal("longitude")
        );
    }
}
