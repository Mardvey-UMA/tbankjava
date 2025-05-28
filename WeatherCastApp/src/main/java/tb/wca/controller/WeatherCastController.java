package tb.wca.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tb.wca.dto.WeatherRequestDTO;
import tb.wca.dto.WeatherResponseDTO;
import tb.wca.exceptions.EmptyCityException;
import tb.wca.exceptions.InvalidCityException;
import tb.wca.service.interfaces.CityWeatherService;

@RestController
@RequestMapping("api/weather")
@RequiredArgsConstructor
public class WeatherCastController {

    private final CityWeatherService cityWeatherService;

    @GetMapping()
    public WeatherResponseDTO weatherCast(@RequestBody WeatherRequestDTO request) throws EmptyCityException, InvalidCityException {
        return cityWeatherService.getWeather(request);
    }
}
