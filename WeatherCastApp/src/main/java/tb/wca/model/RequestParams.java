package tb.wca.model;

import lombok.Builder;
import lombok.Setter;
import tb.wca.entity.CityEntity;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record RequestParams(
        CityEntity city,
        CityGeoModel coords,
        LocalDate startDate,
        LocalTime time,
        LocalDate endDate
) { }

