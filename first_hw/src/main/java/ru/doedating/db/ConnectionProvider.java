package ru.doedating.db;

import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public class ConnectionProvider {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public ConnectionProvider() {
        this.jdbcUrl  = "jdbc:postgresql://localhost:5432/weather_service_db";
        this.username = "postgres";
        this.password = "postgres";
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
