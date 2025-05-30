package tb.wca.model;

import tb.wca.entity.CityEntity;

import java.time.LocalDate;
import java.time.LocalTime;

public record RequestParams(
        CityEntity city,
        CityGeoModel coords,
        LocalDate startDate,
        LocalTime time,
        LocalDate endDate
) { }

