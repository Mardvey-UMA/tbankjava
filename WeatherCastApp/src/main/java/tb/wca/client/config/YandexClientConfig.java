package tb.wca.client.config;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tb.wca.client.decoder.YandexGeoDecoder;
import tb.wca.client.mapper.YandexGeoMapper;

@Configuration
@RequiredArgsConstructor
public class YandexClientConfig {

    private final YandexGeoMapper yandexGeoMapper;

    @Bean
    public RequestInterceptor yandexRequestInterceptor(
            @Value("${api-keys.city}") String apiKey) {
        return template -> template.query("apikey", apiKey);
    }

    @Bean
    public Decoder yandexGeoDecoder() {
        return new YandexGeoDecoder(yandexGeoMapper);
    }
}
