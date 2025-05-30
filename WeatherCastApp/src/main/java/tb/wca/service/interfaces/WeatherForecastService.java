package tb.wca.service.interfaces;

import tb.wca.model.WeatherModel;

import java.util.List;

public interface WeatherForecastService {
    List<WeatherModel> getWeatherForecastByDay(String cityName,String day);
    List<WeatherModel> getWeatherForecastByDayAndHour(String cityName ,String day, String hour);
    List<WeatherModel> getWeatherForecastByRange(String cityName ,String startDate, String endDate);
}
