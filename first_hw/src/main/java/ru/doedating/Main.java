package ru.doedating;

import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.repository.WeatherCastRepository;
import ru.doedating.repository.WeatherCastRepositoryImpl;
import ru.doedating.service.impl.CastGeneratorServiceImpl;
import ru.doedating.service.impl.WeatherCastServiceImpl;
import ru.doedating.service.interfaces.CastGeneratorService;
import ru.doedating.service.interfaces.WeatherCastService;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws EmptyCityException, InvalidCityException {

        CastGeneratorService generator = new CastGeneratorServiceImpl();
        WeatherCastRepository repository = new WeatherCastRepositoryImpl();
        WeatherCastService weatherService = new WeatherCastServiceImpl(generator, repository);

        while (true) {
            System.out.println("Введите город или 0 для выхода:");
            String city = scanner.nextLine();
            if (city.trim().equals("0")) {
                System.out.println("Досвидания ^^");
                break;
            }
            System.out.println(weatherService.generateRandomWeatherCast(city));
        }

    }
}