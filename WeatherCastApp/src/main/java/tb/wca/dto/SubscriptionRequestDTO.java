package tb.wca.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.DateTimeException;
import java.time.LocalTime;
import java.time.ZoneId;

@Builder
public record SubscriptionRequestDTO(
        @NotBlank(message = "City name must not be blank")
        String cityName,

        LocalTime notificationTime,

        String timeZone
) {
        @AssertTrue(message = "notificationTime must be in HH:mm format")
        private boolean isNotificationTimeValid() {
                if (notificationTime == null) return false;
                return notificationTime.getSecond() == 0
                        && notificationTime.getNano()   == 0;
        }

        @AssertTrue(message = "timeZone must be provided and must be a valid ZoneId")
        private boolean isTimeZoneValid() {
                if (timeZone == null || timeZone.isBlank()) {
                        return false;
                }
                try {
                        ZoneId.of(timeZone);
                        return true;
                } catch (DateTimeException e) {
                        return false;
                }
        }
}
