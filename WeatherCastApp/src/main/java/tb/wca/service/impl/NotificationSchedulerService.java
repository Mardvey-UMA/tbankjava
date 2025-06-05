package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tb.wca.avro.WeatherResponseKafkaDTO;
import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.mapper.WeatherResponseMapper;
import tb.wca.repository.SubscriptionRepository;
import tb.wca.service.interfaces.CityWeatherService;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final SubscriptionRepository subscriptionRepository;
    private final CityWeatherService cityWeatherService;
    private final SubscriberSendService subscriberSendService;
    private final WeatherResponseMapper weatherResponseMapper;

    @Scheduled(cron = "0 /10 * * * *")
    public void sendScheduledNotifications() {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        List<SubscriptionEntity> activeSubscriptions = subscriptionRepository.findByIsActive(true);

        for (SubscriptionEntity subscription : activeSubscriptions) {
                ZoneId zoneId = ZoneId.of(subscription.getTimeZone());
                ZonedDateTime userTime = nowUtc.withZoneSameInstant(zoneId);
                LocalTime currentTime = userTime.toLocalTime();

                if (currentTime.getHour() == subscription.getNotificationTime().getHour() &&
                        currentTime.getMinute() == subscription.getNotificationTime().getMinute()) {

                    WeatherRequestDTO request = new WeatherRequestDTO(
                            subscription.getCity().getName(),
                            LocalDate.now().toString(),
                            null, null, null
                    );

                    WeatherResponseDTO weather = cityWeatherService.getWeather(request);

                    WeatherResponseKafkaDTO kafkaDto = weatherResponseMapper.toKafka(weather);

                    subscriberSendService.sendWeatherResponse(
                            kafkaDto,
                            subscription.getUser().getTelegramId().toString()
                    );
                }
        }
    }
}
