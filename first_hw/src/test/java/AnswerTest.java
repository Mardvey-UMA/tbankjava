import org.junit.jupiter.api.Test;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.service.impl.GenerateRandomCastImpl;
import ru.doedating.service.impl.GetWeatherCastServiceImpl;
import ru.doedating.service.interfaces.GenerateRandomCast;
import ru.doedating.service.interfaces.GetWeatherCastService;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AnswerTest {

    @Test
    void answerFormatTest() throws EmptyCityException, InvalidCityException {
        GenerateRandomCast generator = new GenerateRandomCastImpl();
        GetWeatherCastService weatherService = new GetWeatherCastServiceImpl(generator);

        HashMap<String, String> weatherCache = new HashMap<>();

        String city = "Город грехов";

        String wCast = weatherService.generateRandomWeatherCast(city, weatherCache);
        assertTrue(wCast.matches("^Прогноз погоды для города " + city + " на \\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2} " +
                "\nТемпература \\d+\\.\\d{2} C " +
                "\nВлажность \\d+\\.\\d{2} %$"));
    }

    @Test
    void makeHashTest() throws EmptyCityException, InvalidCityException {
        GenerateRandomCast generator = new GenerateRandomCastImpl();
        GetWeatherCastService weatherService = new GetWeatherCastServiceImpl(generator);

        String city1 = "Обнинск";

        String city2 = "Обнинск";

        HashMap<String, String> weatherCache = new HashMap<>();

        String wCast1 = weatherService.generateRandomWeatherCast(city1, weatherCache);
        String wCast2 = weatherService.generateRandomWeatherCast(city2, weatherCache);

        assertEquals(wCast1, wCast2);
    }
}
