package tb.wca.exceptions;

public class NotFoundDataException extends RuntimeException {
    public NotFoundDataException() {super("Не найдена инфоромация за указанную дату");}
}
