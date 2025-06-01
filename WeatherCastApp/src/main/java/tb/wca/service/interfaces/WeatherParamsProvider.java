package tb.wca.service.interfaces;

import tb.wca.model.RequestParams;

public interface WeatherParamsProvider {
    RequestParams byDay(String city, String day);
    RequestParams byDayHour(String city, String day, String hour);
    RequestParams byRange(String city, String start, String end);
}
