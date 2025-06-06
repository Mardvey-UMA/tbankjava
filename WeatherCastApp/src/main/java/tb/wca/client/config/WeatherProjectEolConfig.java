package tb.wca.client.config;

import feign.RequestInterceptor;
import feign.codec.Decoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tb.wca.client.decoder.WeatherProjectEolDecoder;
import tb.wca.client.mapper.WeatherProjectEolMapper;

@Configuration
@RequiredArgsConstructor
public class WeatherProjectEolConfig {

    private final WeatherProjectEolMapper weatherProjectEolMapper;

    @Bean
    public RequestInterceptor weatherRequestInterceptor(
            @Value("${api-keys.weather}") String token) {
        return template -> template.query("token", token);
    }

    @Bean
    public Decoder weatherProjectEolDecoder() {
        return new WeatherProjectEolDecoder(weatherProjectEolMapper);
    }
}
