package ru.doedating.mapper;

import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityMapper {
    public static CityEntity mapToCity(ResultSet rs) throws SQLException {
        CityEntity city = new CityEntity();
        city.setId(rs.getLong("id"));
        city.setName(rs.getString("name"));
        city.setLatitude(rs.getBigDecimal("latitude"));
        city.setLongitude(rs.getBigDecimal("longitude"));
        return city;
    }

    public static WeatherCastEntity mapToWeatherCast(ResultSet rs) throws SQLException {
        WeatherCastEntity cast = new WeatherCastEntity();
        cast.setId(rs.getLong("id"));
        cast.setTemperature(rs.getBigDecimal("temperature"));
        cast.setHumidity(rs.getBigDecimal("humidity"));
        cast.setWindSpeed(rs.getBigDecimal("wind_speed"));
        return cast;
    }
}
