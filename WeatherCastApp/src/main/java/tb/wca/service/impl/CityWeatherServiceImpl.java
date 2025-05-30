package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.exceptions.NotEnoughArgumentsWeatherRequest;
import tb.wca.model.WeatherModel;
import tb.wca.service.interfaces.CityWeatherService;
import tb.wca.service.interfaces.WeatherForecastService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityWeatherServiceImpl implements CityWeatherService {

    private final WeatherForecastService weatherForecastService;

    @Override
    public WeatherResponseDTO getWeather(WeatherRequestDTO request) {
        String city = request.cityName();

        if (request.date() != null && request.hour() != null) {
            List<WeatherModel> weatherModels = weatherForecastService.getWeatherForecastByDayAndHour(
                    city,
                    request.date(),
                    request.hour());
            return new WeatherResponseDTO(weatherModels);

        } else if (request.date() != null) {
            List<WeatherModel> weatherModels = weatherForecastService.getWeatherForecastByDay(
                    city,
                    request.date());
            return new WeatherResponseDTO(weatherModels);

        } else if (request.startDate() != null && request.endDate() != null) {
            List<WeatherModel> weatherModels = weatherForecastService.getWeatherForecastByRange(
                    city,
                    request.startDate(),
                    request.endDate());
            return new WeatherResponseDTO(weatherModels);

        } else {
            throw new NotEnoughArgumentsWeatherRequest();
        }
    }
}
