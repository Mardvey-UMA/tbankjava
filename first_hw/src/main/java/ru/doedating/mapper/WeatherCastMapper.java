package ru.doedating.mapper;

import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.entity.CityEntity;
import ru.doedating.entity.WeatherCastEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WeatherCastMapper {

    @Mapping(target = "cityName", source = "city.name")
    @Mapping(target = "temperature", source = "cast.temperature")
    @Mapping(target = "humidity",    source = "cast.humidity")
    @Mapping(target = "windSpeed",   source = "cast.windSpeed")
    @Mapping(target = "date",        source = "date")
    WeatherCastDTO toDto(CityEntity city, WeatherCastEntity cast, String date);
}
