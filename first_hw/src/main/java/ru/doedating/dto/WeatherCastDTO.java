package ru.doedating.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@RequiredArgsConstructor
public class WeatherCastDTO {
    String cityName;
    BigDecimal temperature;
    BigDecimal humidity;
    BigDecimal windSpeed;
    String date;
}
