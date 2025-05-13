import org.junit.jupiter.api.Test;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.repository.WeatherCastRepository;
import ru.doedating.repository.WeatherCastRepositoryImpl;
import ru.doedating.service.impl.CastGeneratorServiceImpl;
import ru.doedating.service.impl.WeatherCastServiceImpl;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.WeatherCastService;

import static org.junit.jupiter.api.Assertions.*;

public class ThrowsExceptionTests {

    @Test
    void getWeatherCast_WhenCityIsBlank_ThrowsEmptyCityException() {
        CastGeneratorService generator = new CastGeneratorServiceImpl();
        WeatherCastRepository repository = new WeatherCastRepositoryImpl();
        WeatherCastService weatherService = new WeatherCastServiceImpl(generator, repository);

        String city = "  ";

        EmptyCityException emptyCityException = assertThrows(
                EmptyCityException.class,
                () -> weatherService.generateRandomWeatherCast(city)
        );

        assertEquals("Название города не может быть пустым", emptyCityException.getMessage());
    }

    @Test
    void getWeatherCast_WhenInvalidCity_ThrowsInvalidCityException() {
        CastGeneratorService generator = new CastGeneratorServiceImpl();
        WeatherCastRepository repository = new WeatherCastRepositoryImpl();
        WeatherCastService weatherService = new WeatherCastServiceImpl(generator, repository);

        String city = "City17";

        InvalidCityException invalidCityException = assertThrows(
                InvalidCityException.class,
                () -> weatherService.generateRandomWeatherCast(city)
        );

        assertEquals("Город должен содержать только буквы, без цифры и спец символов", invalidCityException.getMessage());
    }
}
