package tb.wca.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import tb.wca.client.YandexGeocodeClient;
import tb.wca.entity.CityEntity;
import tb.wca.mapper.CityGeoMapper;
import tb.wca.mapper.CityGeoMapperImpl;
import tb.wca.model.CityGeoModel;
import tb.wca.repository.CityRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinatesServiceImplTest {

    @Mock
    CityRepository cityRepository;

    @Mock
    YandexGeocodeClient yandexGeocodeClient;

    @Spy
    CityGeoMapper cityGeoMapper = new CityGeoMapperImpl();

    @InjectMocks
    CoordinatesServiceImpl coordinatesService;

    @Test
    void whenCityExists_thenReturnFromDb() {
        String cityName = "Петушки";

        CityEntity cityEntity = new CityEntity(1L, cityName, BigDecimal.valueOf(55.55), BigDecimal.valueOf(55.55));

        when(cityRepository.findByName(cityName))
                .thenReturn(Optional.of(cityEntity));

        CityGeoModel expected = cityGeoMapper.entityToModel(cityEntity);

        CityGeoModel actual   = coordinatesService.getCoordinatesByCityName(cityName);

        assertEquals(expected, actual);
    }

    @Test
    void whenCityNotExists_thenFetchFromApiAndReturn(){
        String cityName = "Петушки";

        when(cityRepository.findByName(cityName))
                .thenReturn(Optional.empty());

        CityGeoModel apiResponse = new CityGeoModel(cityName, BigDecimal.valueOf(55.55), BigDecimal.valueOf(55.55));

        when(yandexGeocodeClient.geocode(cityName, "json"))
                .thenReturn(apiResponse);

        CityEntity saved = cityGeoMapper.modelToEntity(apiResponse);

        when(cityRepository.save(any(CityEntity.class)))
                .thenReturn(saved);

        CityGeoModel actual = coordinatesService.getCoordinatesByCityName(cityName);

        assertEquals(apiResponse, actual);
    }
}