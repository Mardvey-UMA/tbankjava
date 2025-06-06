package tb.wca.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tb.wca.client.config.WeatherProjectEolConfig;
import tb.wca.model.WeatherModel;

import java.util.List;

@FeignClient(
        name = "weatherApiClient",
        url  = "${clients.weather.url}",
        configuration = WeatherProjectEolConfig.class)
public interface WeatherApiClient {

    @GetMapping
    List<WeatherModel> getWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam String date
    );

}
