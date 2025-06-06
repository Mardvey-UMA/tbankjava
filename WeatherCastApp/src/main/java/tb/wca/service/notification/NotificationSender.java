package tb.wca.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.avro.WeatherResponseKafkaDTO;
import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.mapper.WeatherResponseMapper;
import tb.wca.repository.SubscriptionRepository;
import tb.wca.service.interfaces.CityWeatherService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationSender {

    private final CityWeatherService cityWeatherService;
    private final SubscriberSendService subscriberSendService;
    private final WeatherResponseMapper weatherResponseMapper;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationTaskScheduler taskScheduler;

    public void sendForSubscription(SubscriptionEntity subscription) {

        Optional<SubscriptionEntity> freshSubscription = subscriptionRepository.findById(subscription.getId());

        if (freshSubscription.isEmpty() || !freshSubscription.get().getIsActive()) {
            return;
        }

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

        scheduleNextNotification(subscription);
    }

    private void scheduleNextNotification(SubscriptionEntity subscription) {
        ZonedDateTime nextTrigger = ZonedDateTime.now(ZoneId.of(subscription.getTimeZone()))
                .with(subscription.getNotificationTime())
                .plusDays(1);

        taskScheduler.scheduleTask(
                subscription.getId(),
                nextTrigger,
                () -> sendForSubscription(subscription)
        );
    }
}
