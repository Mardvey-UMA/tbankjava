package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class InvalidDateFormatException extends BusinessException {

    public InvalidDateFormatException() {
        super(BusinessErrorCodes.INVALID_DATE_FORMAT);
    }

    public InvalidDateFormatException( String additionalMessage) {
        super(BusinessErrorCodes.INVALID_DATE_FORMAT, additionalMessage);
    }
}
