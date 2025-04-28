package ru.doedating.repository;

import lombok.RequiredArgsConstructor;
import ru.doedating.db.ConnectionProvider;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.mapper.EntityMapper;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class WeatherCastRepositoryImpl implements WeatherCastRepository {
    private final ConnectionProvider cp;

    @Override
    public WeatherCastEntity save(WeatherCastEntity w) {
        String sql = """
                INSERT INTO weather (temperature, humidity, wind_speed)
                VALUES (?,?,?)
                RETURNING id, temperature, humidity, wind_speed
                """;
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setBigDecimal(1, w.getTemperature());
            statement.setBigDecimal(2, w.getHumidity());
            statement.setBigDecimal(3, w.getWindSpeed());
            ResultSet rs = statement.executeQuery();
            rs.next();
            return EntityMapper.mapToWeatherCast(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeatherCastEntity findById(Long id) {
        String sql = "SELECT id, temperature, humidity, wind_speed " +
                "FROM weather WHERE id = ?";
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            return rs.next() ? EntityMapper.mapToWeatherCast(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean existByCityAndDate(String city, String date) {
        String sql = """
            SELECT 1
            FROM city_weather cw
            JOIN city c ON cw.city_id = c.id
            WHERE c.name = ? AND cw.date = ?
            LIMIT 1
        """;
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, city);
            statement.setDate  (2, Date.valueOf(LocalDate.parse(date)));
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public WeatherCastEntity findByCityAndDate(String city, String date) {
        String sql = """
            SELECT w.id, w.temperature, w.humidity, w.wind_speed
            FROM weather w
            JOIN city_weather cw ON w.id = cw.weather_id
            JOIN city c ON cw.city_id = c.id
            WHERE c.name = ? AND cw.date = ?
        """;
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, city);
            statement.setDate  (2, Date.valueOf(LocalDate.parse(date)));
            ResultSet rs = statement.executeQuery();
            if (!rs.next()) return null;
            return EntityMapper.mapToWeatherCast(rs);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<WeatherCastEntity> findAllByCity(String city) {
        String sql = """
            SELECT w.id, w.temperature, w.humidity, w.wind_speed
            FROM weather w
            JOIN city_weather cw ON w.id = cw.weather_id
            JOIN city c ON cw.city_id = c.id
            WHERE c.name = ?
            ORDER BY cw.date DESC
        """;
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, city);
            ResultSet rs = statement.executeQuery();
            List<WeatherCastEntity> list = new ArrayList<>();
            while (rs.next()) list.add(EntityMapper.mapToWeatherCast(rs));
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
