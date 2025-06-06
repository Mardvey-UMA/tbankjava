package tb.wca.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.mapper.WeatherMapper;
import tb.wca.model.WeatherModel;
import tb.wca.repository.CityWeatherRepository;
import tb.wca.repository.WeatherCastRepository;
import tb.wca.service.interfaces.WeatherDataService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class WeatherDataServiceImpl implements WeatherDataService {

    private static final String WEATHER_CACHE = "weather";
    private static final String WEATHER_BY_CITY_DATE = "weatherByCityDate";
    private static final String WEATHER_BY_CITY_DATE_TIME = "weatherByCityDateTime";
    private static final String WEATHER_BY_CITY_RANGE = "weatherByCityRange";

    private final CityWeatherRepository cityWeatherRepository;
    private final WeatherCastRepository weatherCastRepository;
    private final WeatherMapper weatherMapper;

    @Override
    @Cacheable(value = WEATHER_BY_CITY_DATE, key = "#city + '_' + #date.toString()")
    public List<WeatherModel> findByCityAndDate(String city, LocalDate date) {
        return cityWeatherRepository.findByCity_NameAndDate(city, date)
                .stream()
                .map(weatherMapper::cityWeatherEntityToWeatherModel)
                .toList();
    }

    @Override
    @Cacheable(value = WEATHER_BY_CITY_DATE_TIME, key = "#city + '_' + #date.toString() + '_' + #time.toString()")
    public Optional<WeatherModel> findByCityDateTime(String city, LocalDate date, LocalTime time) {
        return cityWeatherRepository.findByCity_NameAndDateAndTime(city, date, time)
                .map(weatherMapper::cityWeatherEntityToWeatherModel);
    }

    @Override
    @Cacheable(value = WEATHER_BY_CITY_RANGE, key = "#city + '_' + #start.toString() + '_' + #end.toString()")
    public List<WeatherModel> findByCityAndRange(String city, LocalDate start, LocalDate end) {
        return cityWeatherRepository.findByCity_NameAndDateBetween(city, start, end)
                .stream()
                .map(weatherMapper::cityWeatherEntityToWeatherModel)
                .toList();
    }
    // TODO Обработать ошибку при сохранении
    @Override
    @Transactional
    @CacheEvict(value = {WEATHER_BY_CITY_DATE, WEATHER_BY_CITY_DATE_TIME, WEATHER_BY_CITY_RANGE}, allEntries = true)
    public void saveForCityAndDate(CityEntity city, LocalDate date, List<WeatherModel> models) {
        List<WeatherCastEntity> weatherEntities = weatherMapper.modelToEntity(models);
        weatherCastRepository.saveAll(weatherEntities);

        List<CityWeatherEntity> cityWeatherEntities = IntStream
                .range(0, models.size())
                .mapToObj(i -> CityWeatherEntity.builder()
                        .city(city)
                        .weather(weatherEntities.get(i))
                        .date(date)
                        .time(models.get(i).time())
                        .build())
                .toList();

        cityWeatherRepository.saveAll(cityWeatherEntities);
    }
    // TODO Обработать ошибку при сохранении
    @Override
    @Transactional
    @CacheEvict(value = {WEATHER_BY_CITY_DATE, WEATHER_BY_CITY_DATE_TIME, WEATHER_BY_CITY_RANGE}, allEntries = true)
    public void saveForCityAndWeatherList(CityEntity city, List<WeatherModel> models) {
        List<WeatherCastEntity> weatherEntities = weatherMapper.modelToEntity(models);
        weatherCastRepository.saveAll(weatherEntities);

        List<CityWeatherEntity> cityWeatherEntities = IntStream
                .range(0, models.size())
                .mapToObj(i -> CityWeatherEntity.builder()
                        .city(city)
                        .weather(weatherEntities.get(i))
                        .date(models.get(i).date())
                        .time(models.get(i).time())
                        .build())
                .toList();

        cityWeatherRepository.saveAll(cityWeatherEntities);
    }
}
