package ru.doedating.service.interfaces;

import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;

import java.time.LocalDate;
import java.util.HashMap;

public interface WeatherCastService {
    WeatherCastDTO getWeatherCastByCityAndDate(String city, String date) throws InvalidCityException, EmptyCityException;
}
