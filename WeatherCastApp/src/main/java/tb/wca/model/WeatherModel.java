package tb.wca.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record WeatherModel(
        LocalDate date,
        LocalTime time,
        double        temp,
        double        feelsLike,
        double        windSpeed,
        double        windDir,
        double        humidity,
        double        pressure,
        double        uvIndex
) { }
