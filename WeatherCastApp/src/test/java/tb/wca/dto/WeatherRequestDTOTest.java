package tb.wca.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeatherRequestDTOTest {

    private static Validator validator;

    String CITY = "Петушки";
    private final String VALID_DATE = "2025-05-31";
    private final String VALID_HOUR = "12";
    private final String VALID_START = "2025-05-31";
    private final String VALID_END = "2025-06-01";

    @BeforeAll
    static void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  "})
    void whenCityIsBlank_ShouldFail(String city) {
        WeatherRequestDTO dto = new WeatherRequestDTO(city, VALID_DATE, null, null, null);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "  ",
            "2025",
            "31-05-2025",
            "2025/05/31"
    })
    void whenDateInvalidFormat_ShouldFail(String date) {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, date, null, null, null);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "  ",
            "26",
            "-1",
            "abc"
    })
    void whenHourInvalid_ShouldFail(String hour) {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, VALID_DATE, null, null, hour);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());

    }

    @Test
    void whenDateAndRangeMissing_ShouldFail() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, null, null, null, null);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());

    }

    @Test
    void whenDateAndRangeTogether_ShouldFail() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, VALID_DATE, VALID_START, VALID_END, null);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());

    }

    @Test
    void whenOnlyStartWithoutEnd_ShouldFail() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, null, VALID_START, null, null);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());

    }

    @Test
    void whenHourWithRange_ShouldFail() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, null, VALID_START, VALID_END, VALID_HOUR);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());

    }

    @Test
    void whenHourWithoutDate_ShouldFail() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, null, null, null, VALID_HOUR);
        var result = validator.validate(dto);
        assertFalse(result.isEmpty());

    }

    @Test
    void whenValidSingleDate_ShouldPass() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, VALID_DATE, null, null, null);
        var result = validator.validate(dto);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenValidSingleDateWithHour_ShouldPass() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, VALID_DATE, null, null, VALID_HOUR);
        var result = validator.validate(dto);
        assertTrue(result.isEmpty());
    }

    @Test
    void whenValidDateRange_ShouldPass() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, null, VALID_START, VALID_END, null);
        var result = validator.validate(dto);
        assertTrue(result.isEmpty());
    }
}