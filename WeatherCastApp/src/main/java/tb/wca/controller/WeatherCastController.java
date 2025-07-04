package tb.wca.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<WeatherResponseDTO> weatherCast(@RequestBody @Valid WeatherRequestDTO request){
        return ResponseEntity.ok(cityWeatherService.getWeather(request));
    }
}
