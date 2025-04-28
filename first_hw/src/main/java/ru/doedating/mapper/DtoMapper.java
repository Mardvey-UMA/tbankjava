package ru.doedating.mapper;

import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;

public class DtoMapper {
    public static WeatherCastDTO mapToWeatherCastDTO(CityEntity city, WeatherCastEntity cast, String date) {
        WeatherCastDTO dto = new WeatherCastDTO();
        dto.setCityName(city.getName());
        dto.setTemperature(cast.getTemperature());
        dto.setHumidity(cast.getHumidity());
        dto.setWindSpeed(cast.getWindSpeed());
        dto.setDate(date);
        return dto;
    }
}
