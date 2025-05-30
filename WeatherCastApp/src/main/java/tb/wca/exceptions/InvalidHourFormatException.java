package tb.wca.exceptions;

public class InvalidHourFormatException extends RuntimeException {
    public InvalidHourFormatException() {super("Час должен быть в диапазоне от 00 до 23");}
}
