package tb.wca.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tb.wca.entity.CityEntity;
import tb.wca.model.CityGeoModel;
import tb.wca.model.RequestParams;
import tb.wca.model.WeatherModel;
import tb.wca.service.interfaces.WeatherApiService;
import tb.wca.service.interfaces.WeatherDataService;
import tb.wca.service.interfaces.WeatherParamsProvider;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherForecastServiceImplTest {

    @Mock
    WeatherParamsProvider weatherParamsProvider;

    @Mock
    WeatherApiService weatherApiService;

    @Mock
    WeatherDataService weatherDataService;

    @InjectMocks
    WeatherForecastServiceImpl weatherForecastService;

    private static final String CITY = "Петушки";
    private static final String MOSCOW = "Москва";
    private static final LocalDate TODAY = LocalDate.of(2025, 6, 2);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    private CityEntity cityEntity;
    private CityEntity moscowEntity;
    private CityGeoModel coords;

    @BeforeEach
    void setUp() {
        coords = CityGeoModel.builder()
                .city(CITY)
                .lat(BigDecimal.ZERO)
                .lon(BigDecimal.ZERO)
                .build();

        cityEntity = CityEntity.builder()
                .name(CITY)
                .longitude(BigDecimal.ZERO)
                .latitude(BigDecimal.ZERO)
                .build();

        moscowEntity = CityEntity.builder()
                .name(MOSCOW)
                .longitude(BigDecimal.ONE)
                .latitude(BigDecimal.ONE)
                .build();
    }

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

    private RequestParams getParamsDefault(LocalDate date) {
        return RequestParams.builder()
                .city(cityEntity)
                .coords(coords)
                .startDate(date)
                .build();
    }

    private RequestParams getParamsForHour(LocalDate date, int hour) {
        return RequestParams.builder()
                .city(cityEntity)
                .coords(coords)
                .startDate(date)
                .time(LocalTime.of(hour, 0))
                .build();
    }

    private RequestParams getParamsForMillionCity(LocalDate date, int hour) {
        return RequestParams.builder()
                .city(moscowEntity)
                .coords(coords)
                .startDate(date)
                .time(LocalTime.of(hour, 0))
                .build();
    }

    private RequestParams getParamsForRange(LocalDate start, LocalDate end) {
        return RequestParams.builder()
                .city(cityEntity)
                .coords(coords)
                .startDate(start)
                .endDate(end)
                .build();
    }

    @Test
    void getCityAndDayFullTimeExistsInDb_ReturnListWeatherModelFromDb() {
        List<WeatherModel> expected = fullDay(TODAY);
        when(weatherParamsProvider.byDay(CITY, TODAY.toString()))
                .thenReturn(getParamsDefault(TODAY));
        when(weatherDataService.findByCityAndDate(CITY, TODAY))
                .thenReturn(expected);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByDay(CITY, TODAY.toString());

        assertEquals(24, result.size());
        assertIterableEquals(expected, result);
        verify(weatherApiService, never()).getWeatherFromApi(any(), any());
        verify(weatherDataService, never()).saveForCityAndDate(any(), any(), any());
    }

    @Test
    void getCityAndDayPartialTimeExistsInDb_ReturnListWeatherModelFromDb() {
        List<WeatherModel> existing = List.of(
                getWeatherModelDefault(TODAY, 0),
                getWeatherModelDefault(TODAY, 1),
                getWeatherModelDefault(TODAY, 2)
        );
        List<WeatherModel> apiResponse = fullDay(TODAY).subList(3, 24);
        List<WeatherModel> expected = fullDay(TODAY);

        when(weatherParamsProvider.byDay(CITY, TODAY.toString()))
                .thenReturn(getParamsDefault(TODAY));
        when(weatherDataService.findByCityAndDate(eq(CITY), eq(TODAY)))
                .thenReturn(existing)
                .thenReturn(expected);
        when(weatherApiService.getWeatherFromApi(coords, TODAY.toString()))
                .thenReturn(apiResponse);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByDay(CITY, TODAY.toString());

        assertEquals(24, result.size());
        assertIterableEquals(expected, result);
        verify(weatherApiService).getWeatherFromApi(coords, TODAY.toString());
        verify(weatherDataService).saveForCityAndDate(cityEntity, TODAY, apiResponse);
    }

    @Test
    void getCityAndDayNothingExistsInDb_ReturnListWeatherModelFromApiAndSaveInDb() {
        List<WeatherModel> apiResponse = fullDay(TODAY);
        when(weatherParamsProvider.byDay(CITY, TODAY.toString()))
                .thenReturn(getParamsDefault(TODAY));
        when(weatherDataService.findByCityAndDate(CITY, TODAY))
                .thenReturn(Collections.emptyList())
                .thenReturn(apiResponse);
        when(weatherApiService.getWeatherFromApi(coords, TODAY.toString()))
                .thenReturn(apiResponse);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByDay(CITY, TODAY.toString());

        assertEquals(24, result.size());
        verify(weatherDataService).saveForCityAndDate(cityEntity, TODAY, apiResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Москва", "Санкт-Петербург"})
    void getMillionCityAndDayAndHourExistsInDb_ReturnListWeatherModelFromDb(String millionCity) {
        int hour = 22;
        WeatherModel expected = getWeatherModelDefault(TODAY, hour);
        RequestParams params = getParamsForMillionCity(TODAY, hour);
        params.city().setName(millionCity);

        when(weatherParamsProvider.byDayHour(millionCity, TODAY.toString(), "22"))
                .thenReturn(params);
        when(weatherDataService.findByCityDateTime(millionCity, TODAY, LocalTime.of(hour, 0)))
                .thenReturn(Optional.of(expected));

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByDayAndHour(
                millionCity, TODAY.toString(), "22");

        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
        verify(weatherApiService, never()).getWeatherFromApi(any(), any());
    }

    @Test
    void getMillionCityAndDayAndHourNotInDb_FetchFullDayAndReturnHour() {
        List<WeatherModel> fullDayData = fullDay(TODAY);
        WeatherModel expected = getWeatherModelDefault(TODAY, 0);

        when(weatherParamsProvider.byDayHour(MOSCOW, TODAY.toString(), "00"))
                .thenReturn(getParamsForMillionCity(TODAY, 0));

        when(weatherDataService.findByCityDateTime(MOSCOW, TODAY, LocalTime.of(0, 0)))
                .thenReturn(Optional.empty());

        when(weatherApiService.getWeatherFromApi(coords, TODAY.toString()))
                .thenReturn(fullDayData);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByDayAndHour(
                MOSCOW, TODAY.toString(), "00");

        assertEquals(expected, result.get(0));
        verify(weatherApiService).getWeatherFromApi(coords, TODAY.toString());
        verify(weatherDataService).saveForCityAndDate(moscowEntity, TODAY, fullDayData);
    }

    @Test
    void getNonMillionCityAndDayAndHourNotInDb_FetchSingleHour() {
        int hour = 22;
        String hourStr = "22";
        WeatherModel expected = getWeatherModelDefault(TODAY, hour);
        String dateTimeParam = TODAY.atTime(hour, 0).toString();

        when(weatherParamsProvider.byDayHour(CITY, TODAY.toString(), hourStr))
                .thenReturn(getParamsForHour(TODAY, hour));
        when(weatherDataService.findByCityDateTime(CITY, TODAY, LocalTime.of(hour, 0)))
                .thenReturn(Optional.empty());
        when(weatherApiService.getWeatherFromApi(coords, dateTimeParam))
                .thenReturn(List.of(expected));

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByDayAndHour(
                CITY, TODAY.toString(), hourStr);

        assertEquals(1, result.size());
        assertEquals(expected, result.get(0));
        verify(weatherApiService).getWeatherFromApi(coords, dateTimeParam);
        verify(weatherDataService).saveForCityAndDate(cityEntity, TODAY, List.of(expected));
    }

    @Test
    void getCityAndRangeFullDataInDb_ReturnDataFromDb() {
        List<WeatherModel> expected = new ArrayList<>();
        expected.addAll(fullDay(TODAY));
        expected.addAll(fullDay(TOMORROW));

        when(weatherParamsProvider.byRange(CITY, TODAY.toString(), TOMORROW.toString()))
                .thenReturn(getParamsForRange(TODAY, TOMORROW));
        when(weatherDataService.findByCityAndRange(CITY, TODAY, TOMORROW))
                .thenReturn(expected);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByRange(
                CITY, TODAY.toString(), TOMORROW.toString());

        assertEquals(48, result.size());
        assertIterableEquals(expected, result);
        verify(weatherApiService, never()).getWeatherFromApi(any(), any());
    }

    @Test
    void getCityAndRangePartialDataInDb_FetchMissingData() {
        List<WeatherModel> existing = new ArrayList<>();
        IntStream.range(0, 5).forEach(hour -> {
            existing.add(getWeatherModelDefault(TODAY, hour));
            existing.add(getWeatherModelDefault(TOMORROW, hour));
        });

        List<WeatherModel> apiResponse = new ArrayList<>();
        apiResponse.addAll(fullDay(TODAY));
        apiResponse.addAll(fullDay(TOMORROW));

        when(weatherParamsProvider.byRange(CITY, TODAY.toString(), TOMORROW.toString()))
                .thenReturn(getParamsForRange(TODAY, TOMORROW));
        when(weatherDataService.findByCityAndRange(CITY, TODAY, TOMORROW))
                .thenReturn(existing)
                .thenReturn(apiResponse);
        when(weatherApiService.getWeatherFromApi(coords, TODAY + "," + TOMORROW))
                .thenReturn(apiResponse);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByRange(
                CITY, TODAY.toString(), TOMORROW.toString());

        assertEquals(48, result.size());
        verify(weatherApiService).getWeatherFromApi(coords, TODAY + "," + TOMORROW);
    }

    @Test
    void getCityAndRangeNoDataInDb_FetchAllData() {
        List<WeatherModel> apiResponse = new ArrayList<>();
        apiResponse.addAll(fullDay(TODAY));
        apiResponse.addAll(fullDay(TOMORROW));

        when(weatherParamsProvider.byRange(CITY, TODAY.toString(), TOMORROW.toString()))
                .thenReturn(getParamsForRange(TODAY, TOMORROW));

        when(weatherDataService.findByCityAndRange(CITY, TODAY, TOMORROW))
                .thenReturn(Collections.emptyList())
                .thenReturn(apiResponse);

        when(weatherApiService.getWeatherFromApi(coords, TODAY + "," + TOMORROW))
                .thenReturn(apiResponse);

        List<WeatherModel> result = weatherForecastService.getWeatherForecastByRange(
                CITY, TODAY.toString(), TOMORROW.toString());

        assertEquals(48, result.size());
        verify(weatherApiService).getWeatherFromApi(coords, TODAY + "," + TOMORROW);
    }

    @Test
    void getCityAndRangeWithInvalidDates_ThrowException() {
        when(weatherParamsProvider.byRange(CITY, TOMORROW.toString(), TODAY.toString()))
                .thenThrow(new IllegalArgumentException());

        assertThrows(IllegalArgumentException.class, () ->
                weatherForecastService.getWeatherForecastByRange(
                        CITY, TOMORROW.toString(), TODAY.toString())
        );
    }
}