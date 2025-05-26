package tb.wca.service.interfaces;

import tb.wca.dto.WeatherCastDTO;
import tb.wca.exceptions.EmptyCityException;
import tb.wca.exceptions.InvalidCityException;

import java.time.LocalDate;
import java.util.HashMap;

public interface WeatherCastService {
    WeatherCastDTO getWeatherCastByCityAndDate(String city, String date) throws InvalidCityException, EmptyCityException;
}
