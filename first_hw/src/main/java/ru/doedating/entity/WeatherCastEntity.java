package ru.doedating.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class WeatherCastEntity {
    private Long id;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal windSpeed;

    public WeatherCastEntity(BigDecimal temperature, BigDecimal humidity, BigDecimal windSpeed) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }

    public WeatherCastEntity(long id, BigDecimal temperature, BigDecimal humidity, BigDecimal windSpeed) {
        this.id = id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
    }
}
