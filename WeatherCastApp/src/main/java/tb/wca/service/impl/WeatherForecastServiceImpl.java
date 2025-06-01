package tb.wca.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tb.wca.model.RequestParams;
import tb.wca.model.WeatherModel;
import tb.wca.service.interfaces.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private final WeatherParamsProvider weatherParamsProvider;
    private final WeatherApiService weatherApiService;
    private final WeatherDataService weatherDataService;

    private static final Integer HOURS_IN_DAY = 24;
    private static final String COMMA = ",";
    private static final Set<String> MILLION_CITIES = Set.of(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург", "Казань",
            "Нижний Новгород", "Челябинск", "Самара", "Омск", "Ростов-на-Дону",
            "Уфа", "Красноярск", "Воронеж", "Пермь", "Волгоград", "Саратов");

    @Override
    public List<WeatherModel> getWeatherForecastByDay(String city, String day) {
        RequestParams params = weatherParamsProvider.byDay(city, day);
        List<WeatherModel> existingForecasts = weatherDataService.findByCityAndDate(city, params.startDate());
        if (existingForecasts.size() >= HOURS_IN_DAY) return existingForecasts;
        List<WeatherModel> apiForecasts = weatherApiService.getWeatherFromApi(params.coords(), day);
        Set<LocalTime> hoursSet = existingForecasts.stream()
                .map(WeatherModel::time)
                .collect(Collectors.toSet());
        List<WeatherModel> newForecast = apiForecasts.stream()
                .filter(m ->
                        !hoursSet.contains(m.time()))
                .toList();
        if (!newForecast.isEmpty()) weatherDataService.saveForCityAndDate(params.city(), params.startDate(), newForecast);
        return weatherDataService.findByCityAndDate(city, params.startDate());
    }


    @Override
    public List<WeatherModel> getWeatherForecastByDayAndHour(String city, String day, String hour) {
        RequestParams params = weatherParamsProvider.byDayHour(city, day, hour);
        if (MILLION_CITIES.contains(params.city().getName())) {
            return weatherDataService.findByCityDateTime(city, params.startDate(), params.time())
                    .map(List::of)
                    .orElseGet(() -> {
                        List<WeatherModel> newForecasts = weatherApiService.getWeatherFromApi(params.coords(), day);
                        weatherDataService.saveForCityAndDate(params.city(), params.startDate(), newForecasts);
                        return newForecasts;
                    });
        }
        return weatherDataService
                .findByCityDateTime(city, params.startDate(), params.time())
                .map(List::of)
                .orElseGet(() -> {
                    String dateTimeParam = LocalDateTime.of(params.startDate(), params.time()).toString();
                    List<WeatherModel> apiForecasts = weatherApiService.getWeatherFromApi(params.coords(), dateTimeParam);
                    weatherDataService.saveForCityAndDate(params.city(), params.startDate(), apiForecasts);
                    return apiForecasts;
                });
    }

    @Override
    public List<WeatherModel> getWeatherForecastByRange(String city, String start, String end) {
        RequestParams params = weatherParamsProvider.byRange(city, start, end);
        List<WeatherModel> existingForecasts = weatherDataService.findByCityAndRange(city, params.startDate(), params.endDate());
        long expected = (params.endDate().toEpochDay() - params.startDate().toEpochDay() + 1) * HOURS_IN_DAY;
        if (existingForecasts.size() >= expected) return existingForecasts;
        String rangeParam = start + COMMA + end;
        List<WeatherModel> apiForecasts = weatherApiService.getWeatherFromApi(params.coords(), rangeParam);
        Map<LocalDate, List<WeatherModel>> forecastsByDay = apiForecasts
                .stream()
                .collect(Collectors.groupingBy(WeatherModel::date));
        List<WeatherModel> toSaveForecasts = new ArrayList<>();
        forecastsByDay.forEach((date, models) -> {
            List<WeatherModel> existingForecast = weatherDataService.findByCityAndDate(city, date);
            if (existingForecast.size() < HOURS_IN_DAY) {
                Set<LocalTime> hoursSet = existingForecast.stream()
                        .map(WeatherModel::time)
                        .collect(Collectors.toSet());
                List<WeatherModel> newForecast = models.stream()
                        .filter(m ->
                                !hoursSet.contains(m.time()))
                        .toList();
                toSaveForecasts.addAll(newForecast);
            }
        });
        if (!toSaveForecasts.isEmpty()) weatherDataService.saveForCityAndWeatherList(params.city(), toSaveForecasts);
        return weatherDataService.findByCityAndRange(city, params.startDate(), params.endDate());
    }
}
