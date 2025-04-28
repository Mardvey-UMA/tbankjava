package ru.doedating.repository;

import lombok.RequiredArgsConstructor;
import ru.doedating.db.ConnectionProvider;
import ru.doedating.entity.CityEntity;
import ru.doedating.mapper.EntityMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class CityRepositoryImpl implements CityRepository {
    private final ConnectionProvider cp;

    @Override
    public CityEntity save(CityEntity city) {
        String sql = """
                INSERT INTO city (name, latitude, longitude)
                VALUES (?,?,?)
                ON CONFLICT (name) DO UPDATE
                    SET latitude = EXCLUDED.latitude,
                        longitude = EXCLUDED.longitude
                RETURNING id, name, latitude, longitude
                """;

        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, city.getName());
            statement.setBigDecimal(2, city.getLatitude());
            statement.setBigDecimal(3, city.getLongitude());

            ResultSet rs = statement.executeQuery();
            rs.next();
            return EntityMapper.mapToCity(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot save city", e);
        }
    }


    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM city WHERE name = ? LIMIT 1";
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            return statement.executeQuery().next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CityEntity findById(Long id) {
        String sql = "SELECT id, name, latitude, longitude FROM city WHERE id = ?";
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            return rs.next() ? EntityMapper.mapToCity(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public CityEntity findByName(String name) {
        String sql = "SELECT id, name, latitude, longitude FROM city WHERE name = ?";
        try (Connection connection = cp.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            ResultSet rs = statement.executeQuery();
            return rs.next() ? EntityMapper.mapToCity(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
