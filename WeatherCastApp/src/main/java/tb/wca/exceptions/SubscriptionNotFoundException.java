package tb.wca.exceptions;

public class SubscriptionNotFoundException extends RuntimeException {
    public SubscriptionNotFoundException(Long teleramId) {
        super("Subscription for user with teleramId = " + telegramId + " not found");
    }
}
