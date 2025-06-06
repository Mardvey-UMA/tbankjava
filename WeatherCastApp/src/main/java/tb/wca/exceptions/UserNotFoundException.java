package tb.wca.exceptions;

import tb.wca.exceptions.enums.BusinessErrorCodes;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(Long telegramId) {
        super(BusinessErrorCodes.USER_NOT_FOUND);
    }
    public UserNotFoundException(String additionalMessage) {
        super(BusinessErrorCodes.NOT_FOUND_DATA, additionalMessage);
    }
}

