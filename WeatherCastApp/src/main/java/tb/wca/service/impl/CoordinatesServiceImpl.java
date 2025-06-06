package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tb.wca.client.YandexGeocodeClient;
import tb.wca.entity.CityEntity;
import tb.wca.exceptions.CityNotFoundException;
import tb.wca.mapper.CityGeoMapper;
import tb.wca.model.CityGeoModel;
import tb.wca.repository.CityRepository;
import tb.wca.service.interfaces.CoordinatesService;

@Service
@RequiredArgsConstructor
public class CoordinatesServiceImpl implements CoordinatesService {

    private final CityRepository cityRepository;
    private final CityGeoMapper cityGeoMapper;
    private final YandexGeocodeClient yandexGeocodeClient;
    private static final String CITIES_CACHE = "cities";

    @Override
    @Cacheable(value = CITIES_CACHE, key = "#cityName")
    public CityGeoModel getCoordinatesByCityName(String cityName) throws CityNotFoundException {
          return cityRepository.findByName(cityName)
                  .map(cityGeoMapper::entityToModel)
                  .orElseGet(() ->{
                      CityGeoModel cityGeoModel = yandexGeocodeClient.geocode(cityName, "json");
                      return cityRepository.findByName(cityGeoModel.city())
                              .map(cityGeoMapper::entityToModel)
                              .orElseGet(
                                      () ->{
                                          CityEntity newCity = cityGeoMapper.modelToEntity(cityGeoModel);
                                          return cityGeoMapper.entityToModel(cityRepository.save(newCity));
                                      }
                              );
                  });
    }

    @Override
    @Cacheable(value = CITIES_CACHE, key = "#cityName + '_entity'")
    public CityEntity getCoordinatesByCityNameReturnSavedEntity(String cityName) throws CityNotFoundException {
        return cityRepository.findByName(cityName)
                .orElseGet(() ->{
                    CityGeoModel cityGeoModel = yandexGeocodeClient.geocode(cityName, "json");
                    return cityRepository.findByName(cityGeoModel.city())
                            .orElseGet(() -> {
                                CityEntity newCity = cityGeoMapper.modelToEntity(cityGeoModel);
                                return cityRepository.save(newCity);
                            });
                });
    }
}
