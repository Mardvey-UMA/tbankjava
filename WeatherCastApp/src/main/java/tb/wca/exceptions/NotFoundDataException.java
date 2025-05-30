package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;
@Getter
public class NotFoundDataException extends BusinessException {
    public NotFoundDataException() {
        super(BusinessErrorCodes.NOT_FOUND_DATA);
    }

    public NotFoundDataException(String additionalMessage) {
        super(BusinessErrorCodes.NOT_FOUND_DATA, additionalMessage);
    }
}
