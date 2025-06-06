package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class EmptyCityException extends BusinessException {

    public EmptyCityException() {
        super(BusinessErrorCodes.EMPTY_CITY);
    }

    public EmptyCityException(String additionalMessage) {
        super(BusinessErrorCodes.EMPTY_CITY);
    }
}
