package tb.wca.exceptions;

import tb.wca.exceptions.enums.BusinessErrorCodes;

public class SubscriptionNotFoundException extends BusinessException {
    public SubscriptionNotFoundException(Long telegramId)
        {
            super(BusinessErrorCodes.SUBSCRIPTION_NOT_FOUND);
        }
    public SubscriptionNotFoundException(String additionalMessage) {
        super(BusinessErrorCodes.SUBSCRIPTION_NOT_FOUND, additionalMessage);
    }
}
