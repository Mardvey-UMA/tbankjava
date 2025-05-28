package tb.wca.service.impl;

import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.repository.CityWeatherRepository;
import tb.wca.service.interfaces.CityWeatherService;

public class CityWeatherServiceImpl implements CityWeatherService {

    private final CityWeatherRepository cityWeatherRepository;
    private final Weather

    @Override
    public WeatherResponseDTO getWeather(WeatherRequestDTO request) {
        return null;
    }
}
