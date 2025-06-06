package tb.wca.exceptions;

import lombok.Getter;
import tb.wca.exceptions.enums.BusinessErrorCodes;

@Getter
public abstract class BusinessException extends RuntimeException{

    private final BusinessErrorCodes errorCode;

    protected BusinessException(BusinessErrorCodes errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    protected BusinessException(BusinessErrorCodes errorCode, String additionalMessage) {
        super(errorCode.getDescription() + ": " + additionalMessage);
        this.errorCode = errorCode;
    }
}
