package tb.wca.exceptions;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException() {super("Дата должна быть в формате yyyy-MM-dd, например 2025-12-31");}
}
