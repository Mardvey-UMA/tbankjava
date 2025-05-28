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
    DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    @Mapping(target = "dateTime",
            expression = "java(LocalDateTime.parse(raw.dtForecast(), ISO))") // ПАРСИТЬ ОТДЕЛЬНО ЧАС ОТДЕЛЬНО ДЕНЬ
    WeatherModel toDto(WeatherProjectEolResponse response);
    List<WeatherModel> toDto(List<WeatherProjectEolResponse> response);
}
