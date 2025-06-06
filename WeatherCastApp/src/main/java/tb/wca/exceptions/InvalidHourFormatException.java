package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class InvalidHourFormatException extends BusinessException {

    public InvalidHourFormatException() {
        super(BusinessErrorCodes.INVALID_HOUR_FORMAT);
    }

    public InvalidHourFormatException(String additionalMessage) {
        super(BusinessErrorCodes.INVALID_HOUR_FORMAT, additionalMessage);
    }
}
