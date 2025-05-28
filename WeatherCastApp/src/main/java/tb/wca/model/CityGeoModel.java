package tb.wca.model;

import java.math.BigDecimal;

public record CityGeoModel(
        String city,
        BigDecimal lon,
        BigDecimal lat)
{ }
