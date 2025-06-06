package tb.wca.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tb.wca.dto.SubscriptionRequestDTO;
import tb.wca.dto.SubscriptionResponseDTO;
import tb.wca.entity.CityEntity;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.entity.UserEntity;
import tb.wca.exceptions.SubscriptionNotFoundException;
import tb.wca.exceptions.UserNotFoundException;
import tb.wca.repository.SubscriptionRepository;
import tb.wca.repository.UserRepository;
import tb.wca.service.interfaces.CoordinatesService;
import tb.wca.service.interfaces.SubscribeService;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static tb.wca.util.TimeCalculator.buildNewSubscription;
import static tb.wca.util.TimeCalculator.validateTimeZone;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CoordinatesService coordinatesService;
    private final NotificationTaskScheduler taskScheduler;
    private final NotificationSender notificationSender;

    @Override
    public SubscriptionResponseDTO createSubscribe(SubscriptionRequestDTO request, Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);

        Optional<SubscriptionEntity> existingSubscription = subscriptionRepository.findByUser(user);
        
        if (existingSubscription.isPresent()) {
            return updateSubscription(existingSubscription.get(), request);
        }

        CityEntity city = getCityEntity(request.cityName());

        SubscriptionEntity newSubscription = buildNewSubscription(user, city, request);
        subscriptionRepository.save(newSubscription);

        scheduleNotification(newSubscription);
        return SubscriptionResponseDTO.of(newSubscription);
    }

    @Override
    public SubscriptionResponseDTO updateSubscribe(SubscriptionRequestDTO request, Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);
        SubscriptionEntity subscription = getSubscriptionOrThrow(user);

        return updateSubscription(subscription, request);
    }

    @Override
    public SubscriptionResponseDTO deleteSubscribe(Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);
        SubscriptionEntity subscription = getSubscriptionOrThrow(user);

        if (subscription != null) {
            user.setSubscription(null);
            subscription.setUser(null);
            userRepository.save(user);
            subscriptionRepository.delete(subscription);
        }else{
            throw new SubscriptionNotFoundException(telegramId);
        }

        taskScheduler.cancelTask(subscription.getId());

        return SubscriptionResponseDTO.message("Deleted subscription");
    }

    private SubscriptionResponseDTO updateSubscription(SubscriptionEntity subscription, SubscriptionRequestDTO request) {
        if (request.cityName() != null) {
            CityEntity city = getCityEntity(request.cityName());
            subscription.setCity(city);
        }
        if (request.notificationTime() != null) {
            subscription.setNotificationTime(request.notificationTime());
        }
        if (request.timeZone() != null) {
            validateTimeZone(request.timeZone());
            subscription.setTimeZone(request.timeZone());
        }

        subscription.setIsActive(true);
        subscriptionRepository.save(subscription);

        taskScheduler.cancelTask(subscription.getId());
        scheduleNotification(subscription);

        return SubscriptionResponseDTO.of(subscription);
    }

    private UserEntity getUserOrThrow(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new UserNotFoundException(telegramId));
    }

    private SubscriptionEntity getSubscriptionOrThrow(UserEntity user) {
//        return subscriptionRepository.findByUser(user)
//                .orElseThrow(() -> new SubscriptionNotFoundException(user.getTelegramId()));
          return user.getSubscription();
    }

    private CityEntity getCityEntity(String cityName) {
        return coordinatesService.getCoordinatesByCityNameReturnSavedEntity(cityName);
    }

    private void scheduleNotification(SubscriptionEntity subscription) {
        ZonedDateTime nextTrigger = calculateNextTrigger(
                subscription.getNotificationTime(),
                ZoneId.of(subscription.getTimeZone())
        );

        taskScheduler.scheduleTask(
                subscription.getId(),
                nextTrigger,
                () -> notificationSender.sendForSubscription(subscription)
        );
    }

    private ZonedDateTime calculateNextTrigger(LocalTime notificationTime, ZoneId timeZone) {
        ZonedDateTime now = ZonedDateTime.now(timeZone);
        ZonedDateTime nextTrigger = now.with(notificationTime);

        if (nextTrigger.isBefore(now)) {
            nextTrigger = nextTrigger.plusDays(1);
        }

        return nextTrigger;
    }
}