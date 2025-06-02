package tb.wca.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tb.wca.client.config.YandexClientConfig;
import tb.wca.model.CityGeoModel;

@FeignClient(
        name = "yandexGeocodeClient",
        url  = "${clients.yandex-geocode.url}",
        configuration = YandexClientConfig.class)
public interface YandexGeocodeClient {

    @GetMapping
    CityGeoModel geocode(@RequestParam("geocode") String city,
                              @RequestParam("format")  String format);
}
