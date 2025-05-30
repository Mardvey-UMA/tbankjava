package tb.wca.service.interfaces;

import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;

public interface CityWeatherService {
    WeatherResponseDTO getWeather(WeatherRequestDTO request);
}
