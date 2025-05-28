package tb.wca.exceptions;

public class InvalidCityException extends RuntimeException {
    public InvalidCityException() {
        super("Город должен содержать только буквы, без цифры и спец символов");
    }
}
