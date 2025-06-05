package tb.wca.exceptions;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tb.wca.exceptions.enums.BusinessErrorCodes;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleCityNotFoundException(CityNotFoundException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(EmptyCityException.class)
    public ResponseEntity<ExceptionResponse> handleEmptyCityException(EmptyCityException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidApiResponse.class)
    public ResponseEntity<ExceptionResponse> handleInvalidApiResponse(InvalidApiResponse ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidCityException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidCityException(InvalidCityException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(InvalidHourFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidHourFormatException(InvalidHourFormatException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(NotEnoughArgumentsWeatherRequestException.class)
    public ResponseEntity<ExceptionResponse> handleNotEnoughArgumentsWeatherRequest(NotEnoughArgumentsWeatherRequestException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(NotFoundDataException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundDataException(NotFoundDataException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleSubscriptionNotFoundException(SubscriptionNotFoundException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException(UserNotFoundException ex) {
        return buildErrorResponse(ex.getErrorCode(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return buildErrorResponse(BusinessErrorCodes.INVALID_ARGUMENTS, details);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        ex.getMostSpecificCause();
        String details = ex.getMostSpecificCause().getMessage();
        return buildErrorResponse(BusinessErrorCodes.DATA_INTEGRITY_VIOLATION, details);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<ExceptionResponse> handleInvalidFormat(InvalidFormatException ex) {
        String targetType = ex.getTargetType().getSimpleName();
        String value = ex.getValue() == null ? "null" : ex.getValue().toString();
        String details = "Не удалось преобразовать '" + value + "' к типу '" + targetType + "'";
        return buildErrorResponse(BusinessErrorCodes.INVALID_FORMAT, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleUnknown(Exception ex) {
        log.error("Необработанная ошибка", ex);
        return buildErrorResponse(BusinessErrorCodes.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
    }

    private ResponseEntity<ExceptionResponse> buildErrorResponse(BusinessErrorCodes code, String message) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(new ExceptionResponse(
                        code.name(),
                        code.getDescription(),
                        message,
                        code.getCode()
                ));
    }
}
