package tb.wca.exceptions;

public class EmptyCityException extends RuntimeException {
    public EmptyCityException() {
        super("Название города не может быть пустым");
    }
}
