package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

import java.util.Optional;

import static tb.wca.util.TimeCalculator.buildNewSubscription;
import static tb.wca.util.TimeCalculator.validateTimeZone;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final CoordinatesService coordinatesService;

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

        return SubscriptionResponseDTO.of(newSubscription);
    }

    @Override
    public SubscriptionResponseDTO updateSubscribe(SubscriptionRequestDTO request, Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);
        SubscriptionEntity subscription = getSubscriptionOrThrow(user);
        return updateSubscription(subscription, request);
    }

    @Override
    public SubscriptionResponseDTO activateSubscription(Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);
        SubscriptionEntity subscription = getSubscriptionOrThrow(user);

        subscription.setIsActive(true);
        subscriptionRepository.save(subscription);

        return SubscriptionResponseDTO.of(subscription);
    }

    @Override
    public SubscriptionResponseDTO deactivateSubscription(Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);
        SubscriptionEntity subscription = getSubscriptionOrThrow(user);

        subscription.setIsActive(false);
        subscriptionRepository.save(subscription);

        return SubscriptionResponseDTO.message("Deactivated");
    }

    @Override
    public SubscriptionResponseDTO deleteSubscribe(Long telegramId) {
        UserEntity user = getUserOrThrow(telegramId);
        SubscriptionEntity subscription = getSubscriptionOrThrow(user);

        subscriptionRepository.delete(subscription);
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

        return SubscriptionResponseDTO.of(subscription);
    }

    private UserEntity getUserOrThrow(Long telegramId) {
        return userRepository.findByTelegramId(telegramId)
                .orElseThrow(() -> new UserNotFoundException(telegramId));
    }

    private SubscriptionEntity getSubscriptionOrThrow(UserEntity user) {
        return subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new SubscriptionNotFoundException(user.getTelegramId()));
    }

    private CityEntity getCityEntity(String cityName) {
        return coordinatesService.getCoordinatesByCityNameReturnSavedEntity(cityName);
    }
}