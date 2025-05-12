package ru.doedating.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.doedating.entity.CityEntity;
import ru.doedating.mapper.CityRowMapper;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CityRepositoryImpl implements CityRepository {

    private final JdbcTemplate jdbcTemplate;
    private final CityRowMapper mapper;

    @Override
    public CityEntity save(CityEntity city) {
        var sql = """
            INSERT INTO city (name, latitude, longitude)
            VALUES (:name, :lat, :lon)
            ON CONFLICT (name) DO UPDATE
              SET latitude = EXCLUDED.latitude,
                  longitude = EXCLUDED.longitude
            RETURNING id, name, latitude, longitude
        """;

        return new NamedParameterJdbcTemplate(jdbcTemplate).queryForObject(sql,
                Map.of("name", city.getName(),
                        "lat",  city.getLatitude(),
                        "lon",  city.getLongitude()),
                mapper);
    }

    @Override
    public CityEntity findByName(String name) {
        var sql = "SELECT id,name,latitude,longitude FROM city WHERE name = ?";
        return DataAccessUtils.singleResult(jdbcTemplate.query(sql, mapper, name));
    }
}

