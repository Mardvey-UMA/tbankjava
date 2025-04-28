package ru.doedating.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
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
}
