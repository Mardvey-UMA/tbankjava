package tb.wca.util;


import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.exceptions.InvalidHourFormatException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class DateTimeValidator {

    private static final String CORRECT_HOUR_FORMAT = "^(0[0-9]|1[0-9]|2[0-3])$";
    private static final String MIDNIGHT = "00";
    private static final String HOURS_IN_DAY_STR = "24";

    public static LocalDate parseDate(String iso) {
        try { return LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE); }
        catch (DateTimeParseException e) { throw new InvalidDateFormatException(); }
    }

    public static LocalTime parseHour(String hh) {
        if (HOURS_IN_DAY_STR.equals(hh)) hh = MIDNIGHT;
        if (!hh.matches(CORRECT_HOUR_FORMAT))
            throw new InvalidHourFormatException();
        return LocalTime.of(Integer.parseInt(hh), 0);
    }
}

