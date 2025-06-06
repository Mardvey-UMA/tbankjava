package tb.wca.model;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
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
