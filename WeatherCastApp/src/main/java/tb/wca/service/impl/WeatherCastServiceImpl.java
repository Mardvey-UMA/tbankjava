package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tb.wca.dto.CastGeneratorDTO;
import tb.wca.dto.WeatherCastDTO;
import tb.wca.entity.CityEntity;
import tb.wca.entity.CityWeatherEntity;
import tb.wca.entity.WeatherCastEntity;
import tb.wca.exceptions.EmptyCityException;
import tb.wca.exceptions.InvalidCityException;
import tb.wca.mapper.WeatherCastMapper;
import tb.wca.repository.CityWeatherRepository;
import tb.wca.repository.CityRepository;
import tb.wca.service.interfaces.CastGeneratorService;
import tb.wca.service.interfaces.CoordinatesService;
import tb.wca.service.interfaces.WeatherCastService;
import tb.wca.repository.WeatherCastRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class WeatherCastServiceImpl implements WeatherCastService {

    private final CastGeneratorService generator;
    private final CoordinatesService coordinatesService;
    private final WeatherCastRepository weatherCastRepository;
    private final CityRepository cityRepository;
    private final CityWeatherRepository cityWeatherRepository;
    @Autowired
    private final WeatherCastMapper mapper;

    @Override
    public WeatherCastDTO getWeatherCastByCityAndDate(String city, String dateStr)
            throws EmptyCityException {

        if (city == null || city.isBlank())  throw new EmptyCityException();

        LocalDate date = LocalDate.parse(dateStr);

        CityEntity cityEntity = cityRepository.findByName(city)
                .orElseGet(() -> {
                    List<BigDecimal> coords = coordinatesService.getCoordinatesByCityName(city);
                    if (coords == null) throw new RuntimeException(new InvalidCityException());
                    return cityRepository.save(
                            CityEntity.builder()
                                    .name(city)
                                    .latitude(coords.get(0))
                                    .longitude(coords.get(1))
                                    .build()
                    );
                });

        WeatherCastEntity cast = cityWeatherRepository
                .findByCity_NameAndDate(city, date)
                .map(CityWeatherEntity::getWeather)
                .orElseGet(() -> {
                    CastGeneratorDTO generated = generator.generateRandomCast();
                    WeatherCastEntity w = weatherCastRepository.save(
                            WeatherCastEntity.builder()
                                    .temperature(generated.getTemperature())
                                    .humidity(generated.getHumidity())
                                    .windSpeed(generated.getWindSpeed())
                                    .build());

                    cityWeatherRepository.save(
                            CityWeatherEntity.builder()
                                    .city(cityEntity)
                                    .weather(w)
                                    .date(date)
                                    .build());
                    return w;
                });


        return mapper.toDto(cityEntity, cast, date.toString());
    }
}