package tb.wca.dto;

import lombok.Builder;
import tb.wca.entity.SubscriptionEntity;
import tb.wca.util.TimeCalculator;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
public record SubscriptionResponseDTO(
        Instant expectedNextNotificationDateTime,
        LocalDateTime expectedNextNotificationDateTimeFormatted,
        String message
) {
    public static SubscriptionResponseDTO of(SubscriptionEntity subscription) {

        Instant nextInstant = TimeCalculator.calculateNextNotification(subscription);
        LocalDateTime nextLocal = TimeCalculator.instantToLocalDateTime(
                nextInstant, subscription.getTimeZone()
        );

        return new SubscriptionResponseDTO(nextInstant, nextLocal, "Success subscribe");
    }

    public static SubscriptionResponseDTO message(String msg) {
        return new SubscriptionResponseDTO(null, null, msg);
    }
}
