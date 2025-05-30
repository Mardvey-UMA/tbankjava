package tb.wca.exceptions;

public class CityNotFoundException extends RuntimeException{
    public CityNotFoundException() {
        super("Город не найден");
    }
}
