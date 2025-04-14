import org.junit.jupiter.api.Test;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.service.impl.GenerateRandomCastImpl;
import ru.doedating.service.impl.GetWeatherCastServiceImpl;
import ru.doedating.service.interfaces.GenerateRandomCast;
import ru.doedating.service.interfaces.GetWeatherCastService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExceptionTests {

    @Test
    void testingBlankCity() {
        GenerateRandomCast generator = new GenerateRandomCastImpl();
        GetWeatherCastService weatherService = new GetWeatherCastServiceImpl(generator);

        HashMap<String, String> weatherCache = new HashMap<>();

        String city = "  ";

        EmptyCityException emptyCityException = assertThrows(
                EmptyCityException.class,
                () -> weatherService.generateRandomWeatherCast(city, weatherCache)
        );

        assertEquals("Название города не может быть пустым", emptyCityException.getMessage());
    }

    @Test
    void testingInvalidCity() {
        GenerateRandomCast generator = new GenerateRandomCastImpl();
        GetWeatherCastService weatherService = new GetWeatherCastServiceImpl(generator);

        HashMap<String, String> weatherCache = new HashMap<>();

        String city = "City17";

        InvalidCityException invalidCityException = assertThrows(
                InvalidCityException.class,
                () -> weatherService.generateRandomWeatherCast(city, weatherCache)
        );

        assertEquals("Город должен содержать только буквы, без цифры и спец символов", invalidCityException.getMessage());
    }
}
