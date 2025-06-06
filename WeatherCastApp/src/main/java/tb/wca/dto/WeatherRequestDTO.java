package tb.wca.dto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
public record WeatherRequestDTO(
        @NotBlank(message = "City name must not be blank")
        String cityName,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        String date,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        String startDate,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        String endDate,

        @Pattern(regexp = "^(0[0-9]|1[0-9]|2[0-3])$")
        String hour
) {
        @AssertTrue(message = "Either 'date' or 'startDate' must be provided")
        private boolean isDateOrStartDateProvided() {
                return (date != null && !date.isBlank()) || (startDate  != null && !startDate.isBlank());
        }

        @AssertTrue(message = "'startDate' and 'endDate' must be provided together")
        private boolean isRangeConsistent() {
                return ((startDate  != null && !startDate.isBlank()) && (endDate != null && !endDate.isBlank()))
                        || (!(startDate  != null && !startDate.isBlank()) && !(endDate != null && !endDate.isBlank()));
        }

        @AssertTrue(message = "Provide either 'date' or ('startDate' and 'endDate')")
        private boolean isEitherSingleDayOrRange() {
                return !((date != null && !date.isBlank()) && (startDate  != null && !startDate.isBlank()));
        }

        @AssertTrue(message = "'hour' must be provided only with 'date'")
        private boolean isHourValid() {
                if (!(hour != null && !hour.isBlank())) {
                        return true;
                }
                return (date != null && !date.isBlank()) && !(startDate  != null && !startDate.isBlank());
        }
}
