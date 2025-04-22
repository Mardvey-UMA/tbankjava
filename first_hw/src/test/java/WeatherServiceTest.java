import org.junit.jupiter.api.Test;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.repository.WeatherCastRepository;
import ru.doedating.repository.WeatherCastRepositoryImpl;
import ru.doedating.service.impl.CastGeneratorServiceImpl;
import ru.doedating.service.impl.WeatherCastServiceImpl;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.WeatherCastService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherServiceTest {

    @Test
    void answerFormatTest() throws EmptyCityException, InvalidCityException {
        CastGeneratorService generator = new CastGeneratorServiceImpl();
        WeatherCastRepository repository = new WeatherCastRepositoryImpl();
        WeatherCastService weatherService = new WeatherCastServiceImpl(generator, repository);

        String city = "Обнинск";

        String wCast = weatherService.generateRandomWeatherCast(city);
        assertTrue(wCast.matches("^Прогноз погоды для города " + city + " на \\d{2}\\.\\d{2}\\.\\d{4} " +
                "\nТемпература \\d+\\.\\d{2} C " +
                "\nВлажность \\d+\\.\\d{2} %$"));
    }

    @Test
    void cityIdempotentTest() throws EmptyCityException, InvalidCityException {
        CastGeneratorService generator = new CastGeneratorServiceImpl();
        WeatherCastRepository repository = new WeatherCastRepositoryImpl();
        WeatherCastService weatherService = new WeatherCastServiceImpl(generator, repository);

        String city1 = "Обнинск";

        String city2 = "Обнинск";

        String wCast1 = weatherService.generateRandomWeatherCast(city1);
        String wCast2 = weatherService.generateRandomWeatherCast(city2);

        assertEquals(wCast1, wCast2);
    }
}
