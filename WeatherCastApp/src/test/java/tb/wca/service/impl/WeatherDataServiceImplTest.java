package tb.wca.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.mapper.WeatherMapper;
import tb.wca.mapper.WeatherMapperImpl;
import tb.wca.model.WeatherModel;
import tb.wca.repository.CityWeatherRepository;
import tb.wca.repository.WeatherCastRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherDataServiceImplTest {

    @Mock
    CityWeatherRepository cityWeatherRepository;

    @Mock
    WeatherCastRepository weatherCastRepository;

    @Spy
    WeatherMapper weatherMapper = new WeatherMapperImpl();

    @InjectMocks
    WeatherDataServiceImpl weatherDataService;

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

    @Test
    void getCityAndDate_findByCityNameAndDate_ReturnListModel(){
        List<WeatherModel> expected = fullDay(TODAY);
        List<CityWeatherEntity> expectedEnitites = expected
                .stream()
                .map(weatherMapper::weatherModelToCityWeatherEntity)
                .toList();

        when(cityWeatherRepository.findByCity_NameAndDate(CITY, TODAY))
                .thenReturn(expectedEnitites);

        List<WeatherModel> actual = weatherDataService.findByCityAndDate(CITY, TODAY);

        assertIterableEquals(actual, expected);
        verify(cityWeatherRepository).findByCity_NameAndDate(CITY, TODAY);
    }

    @Test
    void getCityAndDateAndTime_findByCityNameAndDate_ReturnOptionalModel(){
        Optional<WeatherModel> expected = Optional.of(getWeatherModelDefault(TODAY, Integer.parseInt(HOUR)));
        Optional<CityWeatherEntity> expectedEntity = Optional.of(weatherMapper.weatherModelToCityWeatherEntity(expected.get()));
        LocalTime hourLocalTime = LocalTime.of(Integer.parseInt(HOUR), 0);

        when(cityWeatherRepository.findByCity_NameAndDateAndTime(CITY, TODAY, hourLocalTime))
                .thenReturn(expectedEntity);

        Optional<WeatherModel> actual = weatherDataService.findByCityDateTime(CITY, TODAY, hourLocalTime);

        assertEquals(expected, actual);
        verify(cityWeatherRepository).findByCity_NameAndDateAndTime(CITY, TODAY, hourLocalTime);
    }

    @Test
    void getCityAndDateAndRange_findByCityNameAndRange_ReturnListModel(){
        List<WeatherModel> expected = new ArrayList<>();
        expected.addAll(fullDay(TODAY));
        expected.addAll(fullDay(TOMORROW));

        List<CityWeatherEntity> expectedEnitites = expected
                .stream()
                .map(weatherMapper::weatherModelToCityWeatherEntity)
                .toList();

        when(cityWeatherRepository.findByCity_NameAndDateBetween(CITY, TODAY, TOMORROW))
                .thenReturn(expectedEnitites);

        List<WeatherModel> actual = weatherDataService.findByCityAndRange(CITY, TODAY, TOMORROW);

        assertIterableEquals(actual, expected);
        verify(cityWeatherRepository).findByCity_NameAndDateBetween(CITY, TODAY, TOMORROW);
    }

    @Test
    void saveForCityAndDate_shouldCallRepositoryOnlyTwoTimes() {
        CityEntity city = CityEntity.builder().name(CITY).build();
        LocalDate date = TODAY;
        List<WeatherModel> models = fullDay(date);

        weatherDataService.saveForCityAndDate(city, date, models);

        verify(weatherCastRepository,  times(1)).saveAll(anyList());
        verify(cityWeatherRepository, times(1)).saveAll(anyList());

        verifyNoMoreInteractions(weatherCastRepository, cityWeatherRepository);
    }


    @Test
    void saveForCityAndWeatherList_shouldCallRepositoryOnlyTwoTimes() {
        CityEntity city = CityEntity.builder().name(CITY).build();

        List<WeatherModel> models = new ArrayList<>();
        models.addAll(fullDay(TODAY));
        models.addAll(fullDay(TOMORROW));

        weatherDataService.saveForCityAndWeatherList(city, models);

        verify(weatherCastRepository,  times(1)).saveAll(anyList());

        verify(cityWeatherRepository, times(1)).saveAll(anyList());

        verifyNoMoreInteractions(weatherCastRepository, cityWeatherRepository);
    }
}
