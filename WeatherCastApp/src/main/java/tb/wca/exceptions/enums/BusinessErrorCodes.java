package tb.wca.exceptions.enums;


import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BusinessErrorCodes {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),
    CITY_NOT_FOUND(404, HttpStatus.NOT_FOUND, "City not found"),
    EMPTY_CITY(400, HttpStatus.BAD_REQUEST, "Empty city"),
    INVALID_API_RESPONSE(400, HttpStatus.BAD_REQUEST, "Invalid API response"),
    INVALID_CITY(400, HttpStatus.BAD_REQUEST, "Invalid city"),
    INVALID_DATE_FORMAT(400, HttpStatus.BAD_REQUEST, "Invalid date format"),
    INVALID_HOUR_FORMAT(400, HttpStatus.BAD_REQUEST, "Invalid hour format"),
    NOT_ENOUGH_ARGUMENTS(400, HttpStatus.BAD_REQUEST, "Not enough arguments"),
    NOT_FOUND_DATA(404, HttpStatus.NOT_FOUND, "Not found data"),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    INVALID_ARGUMENTS(400, HttpStatus.BAD_REQUEST, "Invalid arguments"),
    CONSTRAINT_VIOLATION(400, HttpStatus.BAD_REQUEST, "Constraint violation"),
    DATA_INTEGRITY_VIOLATION(400, HttpStatus.BAD_REQUEST, "Data integrity violation"),
    INVALID_FORMAT(400, HttpStatus.BAD_REQUEST, "Invalid JSON format"),
    USER_NOT_FOUND(403, HttpStatus.NOT_FOUND, "User not found"),
    SUBSCRIPTION_NOT_FOUND(403, HttpStatus.NOT_FOUND, "Subscription not found");

    private final int code;
    private final HttpStatus httpStatus;
    private final String description;

    BusinessErrorCodes(int code, HttpStatus httpStatus, String description) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.description = description;
    }
}
