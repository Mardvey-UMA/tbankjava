package tb.wca.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tb.wca.entity.CityEntity;
import tb.wca.exceptions.InvalidDateFormatException;
import tb.wca.mapper.CityGeoMapper;
import tb.wca.mapper.CityGeoMapperImpl;
import tb.wca.model.CityGeoModel;
import tb.wca.model.RequestParams;
import tb.wca.repository.CityRepository;
import tb.wca.service.interfaces.CoordinatesService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherParamsProviderImplTest {

    @Mock
    CoordinatesService coordinatesService;

    @Mock
    CityRepository cityRepository;

    @Spy
    CityGeoMapper cityGeoMapper = new CityGeoMapperImpl();

    @InjectMocks
    WeatherParamsProviderImpl weatherParamsProvider;

    @Test
    void getCityAndDay_ReturnsCityAndDayParamsDto() {
        String cityName = "Петушки";
        String dateString = "2025-05-30";
        LocalDate dateLocalDate = LocalDate.parse(dateString);

        CityGeoModel cityModel = CityGeoModel.builder()
                .city(cityName)
                .lon(BigDecimal.valueOf(55.55))
                .lat(BigDecimal.valueOf(55.55))
                .build();

        CityEntity cityEntity = cityGeoMapper.modelToEntity(cityModel);

        when(coordinatesService.getCoordinatesByCityName(any(cityName.getClass())))
                .thenReturn(cityModel);

        when(cityRepository.findByName(any(cityName.getClass())))
                .thenReturn(Optional.of(cityEntity));

        RequestParams expected = RequestParams.builder()
                .city(cityEntity)
                .coords(cityModel)
                .startDate(dateLocalDate)
                .build();

        RequestParams actual = weatherParamsProvider.byDay(cityName, dateString);

        assertEquals(actual, expected);
    }

    @Test
    void getCityAndDayAndRange_ReturnsCityAndDayAndRangeParamsDto() {

        String cityName = "Петушки";

        String startDateString = "2025-05-30";
        String endDateString = "2025-05-31";

        LocalDate startDateLocalDate = LocalDate.parse(startDateString);
        LocalDate endDateLocalDate = LocalDate.parse(endDateString);

        CityGeoModel cityModel = CityGeoModel.builder()
                .city(cityName)
                .lon(BigDecimal.valueOf(55.55))
                .lat(BigDecimal.valueOf(55.55))
                .build();

        CityEntity cityEntity = cityGeoMapper.modelToEntity(cityModel);

        when(coordinatesService.getCoordinatesByCityName(any(cityName.getClass())))
                .thenReturn(cityModel);

        when(cityRepository.findByName(any(cityName.getClass())))
                .thenReturn(Optional.of(cityEntity));

        RequestParams expected = RequestParams.builder()
                .city(cityEntity)
                .coords(cityModel)
                .startDate(startDateLocalDate)
                .endDate(endDateLocalDate)
                .build();

        RequestParams actual = weatherParamsProvider.byRange(cityName, startDateString, endDateString);

        assertEquals(actual, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "00", "01", "02", "03", "04", "05",
            "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17",
            "18", "19", "20", "21", "22", "23"
    })
    void getCityAndDayAndHour_ReturnsCityAndDayAndHourParamsDto(String hour) {
        String cityName = "Петушки";
        String dateString = "2025-05-30";
        LocalDate dateLocalDate = LocalDate.parse(dateString);
        LocalTime hourFormatted = LocalTime.of(Integer.parseInt(hour), 0);

        CityGeoModel cityModel = CityGeoModel.builder()
                .city(cityName)
                .lon(BigDecimal.valueOf(55.55))
                .lat(BigDecimal.valueOf(55.55))
                .build();

        CityEntity cityEntity = cityGeoMapper.modelToEntity(cityModel);

        when(coordinatesService.getCoordinatesByCityName(any(cityName.getClass())))
                .thenReturn(cityModel);

        when(cityRepository.findByName(any(cityName.getClass())))
                .thenReturn(Optional.of(cityEntity));

        RequestParams expected = RequestParams.builder()
                .city(cityEntity)
                .time(hourFormatted)
                .coords(cityModel)
                .startDate(dateLocalDate)
                .build();

        RequestParams actual = weatherParamsProvider.byDayHour(cityName, dateString, hour);

        assertEquals(actual, expected);
    }

    @Test
    void getCityAndDayAndIncorrectRange_ThrowsInvalidDateFormatException() {

        String cityName = "Петушки";
        String startDateString = "2025-05-31";
        String endDateString = "2025-05-30";

        assertThrows(
                InvalidDateFormatException.class,
                () -> {
                    weatherParamsProvider.byRange(cityName, startDateString, endDateString);
                }
        );
    }

}