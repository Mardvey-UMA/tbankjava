package tb.wca.exceptions;

public class EmptyCityException extends Exception {

    public EmptyCityException() {
        super("Название города не может быть пустым");
    }

}
