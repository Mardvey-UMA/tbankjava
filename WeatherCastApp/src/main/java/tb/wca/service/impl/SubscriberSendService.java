package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tb.wca.avro.WeatherResponseKafkaDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriberSendService {

    private final KafkaTemplate<String, WeatherResponseKafkaDTO> weatherResponseTemplate;

    public void sendWeatherResponse(WeatherResponseKafkaDTO response, String telegramId) {
        weatherResponseTemplate.send(
                "weather-subscribers",
                telegramId,
                response
        );
    }
}
