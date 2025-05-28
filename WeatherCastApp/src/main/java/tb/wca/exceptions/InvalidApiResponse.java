package tb.wca.exceptions;

public class InvalidApiResponse extends RuntimeException {
    public InvalidApiResponse(String message) {super(String.format("Ошибка в API %s", message));}
}
