package tb.wca.dto;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record WeatherRequestDTO(
        @NotBlank(message = "City name must not be blank")
        String cityName,

        String date,

        String startDate,

        String endDate,

        String hour
) {
        @AssertTrue(message = "Either date or startDate must be provided")
        public boolean isDateOrStartDateProvided() {
                return (date != null && !date.isBlank()) || (startDate != null && !startDate.isBlank());
        }

}
