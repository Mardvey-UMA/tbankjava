package ru.doedating.repository;

import lombok.RequiredArgsConstructor;
import ru.doedating.db.ConnectionProvider;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;
import ru.doedating.exceptions.InvalidCityException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

@RequiredArgsConstructor
public class CastCityRepositoryImpl implements CastCityRepository {

    private final ConnectionProvider cp;

    @Override
    public void save(CityEntity city, WeatherCastEntity cast, String date) throws InvalidCityException {
        if (city.getId() == null || cast.getId() == null)
            throw new InvalidCityException();

        String sql = """
                INSERT INTO city_weather (city_id, weather_id, date)
                VALUES (?,?,?)
                ON CONFLICT (city_id, date) DO UPDATE
                    SET weather_id = EXCLUDED.weather_id
            """;

        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong     (1, city.getId());
            statement.setLong     (2, cast.getId());
            statement.setDate     (3, Date.valueOf(LocalDate.parse(date)));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
