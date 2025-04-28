package ru.doedating.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@RequiredArgsConstructor
public class CityEntity {
    private Long id;
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public CityEntity(String city, BigDecimal latitude, BigDecimal longitude) {
        this.name = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
