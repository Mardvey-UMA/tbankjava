package tb.wca.dto;

import lombok.Builder;
import org.springframework.cglib.core.Local;
import tb.wca.entity.CityEntity;
import tb.wca.model.CityGeoModel;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record DateCoordsCityDTO(
    CityGeoModel coordinates,
    CityEntity cityEntity,
    LocalDate requestDate,
    LocalDate startDate,
    LocalDate endDate,
    LocalTime requestTime
) { }
