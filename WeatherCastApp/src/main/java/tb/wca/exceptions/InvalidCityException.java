package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class InvalidCityException extends BusinessException {

    public InvalidCityException() {
        super(BusinessErrorCodes.INVALID_CITY);
    }

    public InvalidCityException(String additionalMessage) {
        super(BusinessErrorCodes.INVALID_CITY, additionalMessage);
    }
}
