package tb.wca.exceptions;

public class NotEnoughArgumentsWeatherRequest extends RuntimeException {
    public NotEnoughArgumentsWeatherRequest() {
        super("Недостаточно аргументов для получения прогноза погоды");
    }
}
