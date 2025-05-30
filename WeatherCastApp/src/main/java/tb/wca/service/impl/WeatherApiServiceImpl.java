package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.client.WeatherApiClient;
import tb.wca.model.CityGeoModel;
import tb.wca.model.WeatherModel;
import tb.wca.service.interfaces.WeatherApiService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherApiServiceImpl implements WeatherApiService {

    private final WeatherApiClient weatherApiClient;

    @Override
    public List<WeatherModel> getWeatherFromApi(CityGeoModel coordinates, String requestParam) {
        return weatherApiClient.getWeather(
                coordinates.lat().doubleValue(),
                coordinates.lon().doubleValue(),
                requestParam);
    }
}
