package tb.wca.service.interfaces;

import tb.wca.entity.CityEntity;
import tb.wca.model.WeatherModel;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface WeatherDataService {
    List<WeatherModel> findByCityAndDate(String city, LocalDate date);
    Optional<WeatherModel> findByCityDateTime(String city, LocalDate date, LocalTime time);
    List<WeatherModel> findByCityAndRange(String city, LocalDate start, LocalDate end);
    void saveForCityAndDate(CityEntity city, LocalDate date, List<WeatherModel> models);
}
