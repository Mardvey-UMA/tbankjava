package tb.wca.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.client.dto.WeatherProjectEolResponse;
import tb.wca.model.WeatherModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring",
        imports = {LocalDateTime.class, DateTimeFormatter.class})
public interface WeatherProjectEolMapper {
    @Mapping(target = "date", expression = "java(LocalDateTime.parse(raw.dtForecast(), java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate())")
    @Mapping(target = "time", expression = "java(LocalDateTime.parse(raw.dtForecast(), java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalTime())")
    @Mapping(source = "temp", target = "temp")
    @Mapping(source = "feelsLike", target = "feelsLike")
    @Mapping(source = "windSpeed", target = "windSpeed")
    @Mapping(source = "windDir", target = "windDir")
    @Mapping(source = "humidity", target = "humidity")
    @Mapping(source = "pressure", target = "pressure")
    @Mapping(source = "uvIndex", target = "uvIndex")
    WeatherModel toDto(WeatherProjectEolResponse raw);

    List<WeatherModel> toDto(List<WeatherProjectEolResponse> response);
}
