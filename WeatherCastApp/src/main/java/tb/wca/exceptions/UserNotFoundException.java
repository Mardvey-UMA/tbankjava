package tb.wca.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long telegramId) {
        super("User with telegramId=" + telegramId + " not found");
    }
}

