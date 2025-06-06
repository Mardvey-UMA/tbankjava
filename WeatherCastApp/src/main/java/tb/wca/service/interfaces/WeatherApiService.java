package tb.wca.service.interfaces;

import tb.wca.model.CityGeoModel;
import tb.wca.model.WeatherModel;

import java.util.List;

public interface WeatherApiService {
    List<WeatherModel> getWeatherFromApi(CityGeoModel coordinates, String requestParam);
}
