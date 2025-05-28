package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.client.YandexGeocodeClient;
import tb.wca.client.dto.YandexGeoResponse;
import tb.wca.client.mapper.YandexGeoMapper;
import tb.wca.entity.CityEntity;
import tb.wca.mapper.CityGeoMapper;
import tb.wca.model.CityGeoModel;
import tb.wca.exceptions.CityNotFoundException;
import tb.wca.repository.CityRepository;
import tb.wca.service.interfaces.CoordinatesService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoordinatesServiceImpl implements CoordinatesService {

    private final CityRepository cityRepository;
    private final CityGeoMapper cityGeoMapper;
    private final YandexGeocodeClient yandexGeocodeClient;

    @Override
    public CityGeoModel getCoordinatesByCityName(String cityName) throws CityNotFoundException {
        Optional<CityEntity> currentCity = cityRepository.findByName(cityName);
        if (currentCity.isPresent()){
            return cityGeoMapper.entityToModel(currentCity.get());
        }else{
            CityEntity newCity = cityGeoMapper.modelToEntity(yandexGeocodeClient.geocode(cityName, "json"));
            return cityGeoMapper.entityToModel(cityRepository.save(newCity));
        }
    }
}
