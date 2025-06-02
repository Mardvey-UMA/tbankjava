package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.exceptions.NotEnoughArgumentsWeatherRequestException;
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
        List<WeatherModel> weatherModels;

        if (request.date() != null && request.hour() != null) {

            weatherModels = weatherForecastService.getWeatherForecastByDayAndHour(
                    request.cityName(),
                    request.date(),
                    request.hour());

        } else if (request.date() != null) {

            weatherModels = weatherForecastService.getWeatherForecastByDay(
                    request.cityName(),
                    request.date());

        } else if (request.startDate() != null && request.endDate() != null) {

            weatherModels = weatherForecastService.getWeatherForecastByRange(
                    request.cityName(),
                    request.startDate(),
                    request.endDate());
        } else {
            throw new NotEnoughArgumentsWeatherRequestException();
        }
        return new WeatherResponseDTO(weatherModels);
    }
}
