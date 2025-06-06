package tb.wca.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.model.WeatherModel;
import tb.wca.service.interfaces.WeatherForecastService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CityWeatherServiceImplTest {

    private static final String CITY = "Петушки";
    private static final LocalDate TODAY = LocalDate.of(2025, 6, 2);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private static final String TODAY_STRING = "2025-06-02";
    private static final String HOUR = "12";

    private static final String START = "2025-05-31";
    private static final String END = "2025-06-03";

    private static WeatherModel getWeatherModelDefault(LocalDate date, int hour) {
        return WeatherModel.builder()
                .date(date)
                .time(LocalTime.of(hour, 0))
                .temp(30)
                .feelsLike(29)
                .windSpeed(6)
                .windDir(90)
                .humidity(45)
                .pressure(1000)
                .uvIndex(0.5)
                .build();
    }

    private List<WeatherModel> fullDay(LocalDate date) {
        return IntStream.range(0, 24)
                .mapToObj(hour -> getWeatherModelDefault(date, hour))
                .toList();
    }


    @Mock
    WeatherForecastService weatherForecastService;

    @InjectMocks
    CityWeatherServiceImpl cityWeatherService;

    @Test
    void getWeatherForecastByDayAndHour_ShouldReturnWeatherForecastByDayAndHour() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, TODAY_STRING, null, null, HOUR);
        List<WeatherModel> expected = List.of(getWeatherModelDefault(TODAY, Integer.parseInt(HOUR)));

        when(weatherForecastService.getWeatherForecastByDayAndHour(CITY, TODAY_STRING, HOUR))
                .thenReturn(expected);

        WeatherResponseDTO actual = cityWeatherService.getWeather(dto);

        List<WeatherModel> actualForecasts = actual.forecasts();

        assertIterableEquals(actualForecasts, expected);
        verify(weatherForecastService).getWeatherForecastByDayAndHour(CITY, TODAY_STRING, HOUR);
    }

    @Test
    void getWeatherForecastByDay_ShouldReturnWeatherForecastByDay() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, TODAY_STRING, null, null, null);

        List<WeatherModel> expected = fullDay(TODAY);

        when(weatherForecastService.getWeatherForecastByDay(CITY, TODAY_STRING))
                .thenReturn(expected);

        WeatherResponseDTO actual = cityWeatherService.getWeather(dto);

        List<WeatherModel> actualForecasts = actual.forecasts();

        assertIterableEquals(actualForecasts, expected);
        verify(weatherForecastService).getWeatherForecastByDay(CITY, TODAY_STRING);
    }

    @Test
    void getWeatherForecastByRange_ShouldReturnWeatherForecastByRange() {
        WeatherRequestDTO dto = new WeatherRequestDTO(CITY, null, START, END, null);

        List<WeatherModel> expected = new ArrayList<>();
        expected.addAll(fullDay(TODAY));
        expected.addAll(fullDay(TOMORROW));

        when(weatherForecastService.getWeatherForecastByRange(CITY, START, END))
                .thenReturn(expected);

        WeatherResponseDTO actual = cityWeatherService.getWeather(dto);

        List<WeatherModel> actualForecasts = actual.forecasts();

        assertIterableEquals(actualForecasts, expected);
        verify(weatherForecastService).getWeatherForecastByRange(CITY, START, END);
    }
}