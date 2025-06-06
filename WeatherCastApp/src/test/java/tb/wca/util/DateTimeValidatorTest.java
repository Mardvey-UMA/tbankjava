package tb.wca.util;

import org.junit.jupiter.api.Test;

import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.exceptions.InvalidHourFormatException;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class DateTimeValidatorTest {

    @Test
    void getRawCorrectStringDateAndParseIt(){
        String dateString = "2020-01-01";
        LocalDate expectedDate = LocalDate.parse(dateString);
        LocalDate methodDate = DateTimeValidator.parseDate(dateString);
        assertEquals(expectedDate, methodDate);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23"
    })
    void getRawStringCorrectHourAndParseIt(String hour){
        LocalTime expected = LocalTime.of(Integer.parseInt(hour), 0);
        LocalTime actual   = DateTimeValidator.parseHour(hour);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-31-31",
            "31-31-2020",
            "ababababab",
            "-100000000"
    })
    void getRawIncorrectStringDateAndParseIt_ThrowsInvalidDateFormatException(String dateString){
        assertThrows(
                InvalidDateFormatException.class,
                () -> DateTimeValidator.parseDate(dateString)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "25", "78", "125", "-100", "-01", " 00 ", " 11 "
    })
    void getRawIncorrectStringHourAndParseIt_ThrowsInvalidDateFormatException(String hour){
        assertThrows(
                InvalidHourFormatException.class,
                () -> DateTimeValidator.parseHour(hour)
        );
    }
}
