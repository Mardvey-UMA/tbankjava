package tb.wca.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tb.wca.client.dto.WeatherProjectEolResponse;
import tb.wca.model.WeatherModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring",
        imports = {LocalDateTime.class, DateTimeFormatter.class})
public interface WeatherProjectEolMapper {
    @Mapping(target = "date", source = "dtForecast", qualifiedByName = "toDate")
    @Mapping(target = "time", source = "dtForecast", qualifiedByName = "toTime")
    @Mapping(source = "temp",      target = "temp")
    @Mapping(source = "feelsLike", target = "feelsLike")
    @Mapping(source = "windSpeed", target = "windSpeed")
    @Mapping(source = "windDir",   target = "windDir")
    @Mapping(source = "humidity",  target = "humidity")
    @Mapping(source = "pressure",  target = "pressure")
    @Mapping(source = "uvIndex",   target = "uvIndex")
    WeatherModel toDto(WeatherProjectEolResponse raw);

    @Named("toDate")
    default LocalDate toDate(String isoDateTime) {
        return LocalDateTime.parse(
                isoDateTime,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ).toLocalDate();
    }

    @Named("toTime")
    default LocalTime toTime(String isoDateTime) {
        return LocalDateTime.parse(
                isoDateTime,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        ).toLocalTime();
    }

    List<WeatherModel> toDto(List<WeatherProjectEolResponse> response);
}
