package tb.wca.dto;

import lombok.Builder;
import lombok.Setter;

import java.time.Instant;

@Builder
public record SubscriptionResponseDTO(
        Instant expectedNextNotificationDateTime
) { }
