package tb.wca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import tb.wca.avro.WeatherModelForResponseKafkaDTO;
import tb.wca.avro.WeatherResponseKafkaDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.model.WeatherModel;

@Mapper(componentModel = "spring")
public interface WeatherResponseMapper {

    @Mapping(target = "forecasts", source = "forecasts")
    WeatherResponseKafkaDTO toKafka(WeatherResponseDTO responseDTO);

    WeatherModelForResponseKafkaDTO toKafka(WeatherModel responseDTO);
}

