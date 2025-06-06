package tb.wca.client.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import lombok.RequiredArgsConstructor;
import tb.wca.client.dto.WeatherProjectEolResponse;
import tb.wca.client.mapper.WeatherProjectEolMapper;
import tb.wca.exceptions.NotFoundDataException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
public class WeatherProjectEolDecoder implements Decoder {

    private final JacksonDecoder jacksonDecoder = new JacksonDecoder();
    private final WeatherProjectEolMapper weatherProjectEolMapper;

    @Override
    public Object decode(Response response, Type type) throws IOException {
        Object decoded = jacksonDecoder.decode(response, List.class);

        List<?> responseList = (List<?>) decoded;

        if (responseList.size() == 1 && responseList.get(0) instanceof String) {
            throw new NotFoundDataException("no weather forecast found for the specified date");
        }

        ObjectMapper mapper = new ObjectMapper();

        return responseList.stream()
                .map(item -> {
                    return weatherProjectEolMapper.toDto(
                            mapper.convertValue(
                                    item,
                                    WeatherProjectEolResponse.class
                            )
                    );
                })
                .toList();
    }
}

