package ru.doedating;

import com.fasterxml.jackson.core.JsonFactory;
import ru.doedating.db.ConnectionProvider;
import ru.doedating.dto.WeatherCastDTO;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.repository.*;
import ru.doedating.service.impl.CastGeneratorServiceImpl;
import ru.doedating.service.impl.CoordinatesServiceImpl;
import ru.doedating.service.impl.WeatherCastServiceImpl;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.CoordinatesService;
import ru.doedating.service.interfaces.WeatherCastService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws EmptyCityException, InvalidCityException {

        //String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        ConnectionProvider connectionProvider = new ConnectionProvider();

        CityRepository cityRepository = new CityRepositoryImpl(connectionProvider);
        WeatherCastRepository weatherCastRepository = new WeatherCastRepositoryImpl(connectionProvider);
        CastCityRepository castCityRepository = new CastCityRepositoryImpl(connectionProvider);


        CastGeneratorService castGeneratorService = new CastGeneratorServiceImpl();
        CoordinatesService coordinatesService = new CoordinatesServiceImpl();
        WeatherCastService weatherCastService = new WeatherCastServiceImpl(
                castGeneratorService,
                coordinatesService,
                weatherCastRepository,
                cityRepository,
                castCityRepository
        );

        while (true) {
            System.out.println("Введите город или 0 для выхода:");
            String city = scanner.nextLine();

            if (city.trim().equals("0")) {
                System.out.println("Досвидания ^^");
                break;
            }
            WeatherCastDTO weather = weatherCastService.getWeatherCastByCityAndDate(city, date);

            System.out.println("\nПрогноз погоды для города " + city + " на " + date + ":");
            System.out.println("Температура: " + weather.getTemperature() + "C");
            System.out.println("Влажность: " + weather.getHumidity() + "%");
            System.out.println("Скорость ветра: " + weather.getWindSpeed() + " м/с");
        }
    }
}