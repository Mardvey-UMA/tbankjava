package tb.wca.dto;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record SubscriptionInfo(
        String cityName,

        LocalTime notificationTime,

        String timeZone
) {}
