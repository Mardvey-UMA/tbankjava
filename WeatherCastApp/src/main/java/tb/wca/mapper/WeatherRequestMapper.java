package tb.wca.mapper;

import org.mapstruct.Mapper;
import tb.wca.avro.WeatherRequestKafkaDTO;
import tb.wca.dto.WeatherRequestDTO;

@Mapper(componentModel = "spring")
public interface WeatherRequestMapper {
    WeatherRequestDTO toDto(WeatherRequestKafkaDTO weatherRequestKafkaDTO);
}
