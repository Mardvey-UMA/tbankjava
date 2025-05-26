package tb.wca.exceptions;

public class InvalidCityException extends Exception {

    public InvalidCityException() {
        super("Город должен содержать только буквы, без цифры и спец символов");
    }

}
