package tb.wca.util;

import tb.wca.dto.SubscriptionRequestDTO;
import tb.wca.entity.CityEntity;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.entity.UserEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimeCalculator {
    public static Instant calculateNextNotification(SubscriptionEntity subscription) {
        if (!subscription.getIsActive()) {
            return null;
        }

        ZoneId zoneId = ZoneId.of(subscription.getTimeZone());
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime nextNotification = now.with(subscription.getNotificationTime());

        if (now.isAfter(nextNotification)) {
            nextNotification = nextNotification.plusDays(1);
        }

        return nextNotification.toInstant();
    }

    public static void validateTimeZone(String timeZone) {
        try {
            ZoneId.of(timeZone);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid time zone: " + timeZone);
        }
    }

    public static LocalDateTime instantToLocalDateTime(Instant instant, String timeZone) {
        return instant == null ? null
                : LocalDateTime.ofInstant(instant, ZoneId.of(timeZone));
    }

    public static SubscriptionEntity buildNewSubscription(
            UserEntity user,
            CityEntity city,
            SubscriptionRequestDTO request
    ) {
        validateTimeZone(request.timeZone());

        return SubscriptionEntity.builder()
                .user(user)
                .city(city)
                .timeZone(request.timeZone())
                .notificationTime(request.notificationTime())
                .isActive(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}
