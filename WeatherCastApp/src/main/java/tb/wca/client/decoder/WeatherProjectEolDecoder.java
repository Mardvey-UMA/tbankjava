package tb.wca.client.decoder;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import lombok.RequiredArgsConstructor;
import tb.wca.client.dto.WeatherProjectEolResponse;
import tb.wca.client.dto.YandexGeoResponse;
import tb.wca.client.mapper.WeatherProjectEolMapper;
import tb.wca.exceptions.NotFoundDataException;
import tb.wca.model.WeatherModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
public class WeatherProjectEolDecoder implements Decoder {

    private final JacksonDecoder jacksonDecoder = new JacksonDecoder();
    private final WeatherProjectEolMapper weatherProjectEolMapper;

    @Override
    public Object decode(Response response, Type type) throws IOException {
        Object decoded = jacksonDecoder.decode(response, type);
        if (decoded instanceof List<?> list) {
            if (!list.isEmpty() && list.get(0) instanceof WeatherProjectEolResponse) {
                List<WeatherProjectEolResponse> dtoList = (List<WeatherProjectEolResponse>) list;
                return weatherProjectEolMapper.toDto(dtoList);
            }
        }
        throw new NotFoundDataException();
    }
}
