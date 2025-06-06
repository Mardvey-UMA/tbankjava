package tb.wca.client.decoder;

import feign.Response;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import lombok.RequiredArgsConstructor;
import tb.wca.client.dto.YandexGeoResponse;
import tb.wca.client.mapper.YandexGeoMapper;
import tb.wca.exceptions.CityNotFoundException;

import java.io.IOException;
import java.lang.reflect.Type;

@RequiredArgsConstructor
public class YandexGeoDecoder implements Decoder {

    private final JacksonDecoder jacksonDecoder = new JacksonDecoder();
    private final YandexGeoMapper yandexGeoMapper;
    private static final String ZERO = "0";

    @Override
    public Object decode(Response response, Type type) throws IOException {
        Object decoded = jacksonDecoder.decode(response, YandexGeoResponse.class);

        if (decoded instanceof YandexGeoResponse yandexGeoResponse) {
            String found = yandexGeoResponse.response()
                    .geoObjectCollection()
                    .metaDataProperty()
                    .geocoderResponseMetaData()
                    .found();
            if (ZERO.equals(found)) {
                throw new CityNotFoundException();
            }
            return yandexGeoMapper.toDto(yandexGeoResponse);
        }
        return decoded;
    }
}
