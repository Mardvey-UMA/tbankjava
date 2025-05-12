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

    public CityEntity(long id, String name, BigDecimal latitude, BigDecimal longitude) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
