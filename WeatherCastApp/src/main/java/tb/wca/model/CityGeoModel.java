package tb.wca.model;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CityGeoModel(
        String city,
        BigDecimal lon,
        BigDecimal lat)
{ }
