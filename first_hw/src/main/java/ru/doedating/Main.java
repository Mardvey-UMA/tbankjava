package ru.doedating;

import lombok.RequiredArgsConstructor;
import ru.doedating.exceptions.EmptyCityException;
import ru.doedating.exceptions.InvalidCityException;
import ru.doedating.service.impl.GenerateRandomCastImpl;
import ru.doedating.service.impl.GetWeatherCastServiceImpl;
import ru.doedating.service.interfaces.GenerateRandomCast;
import ru.doedating.service.interfaces.GetWeatherCastService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws EmptyCityException, InvalidCityException {

        GenerateRandomCast generator = new GenerateRandomCastImpl();
        GetWeatherCastService weatherService = new GetWeatherCastServiceImpl(generator);

        HashMap<String, String> weatherCache = new HashMap<>();

        while (true) {
            System.out.println("Введите город или 0 для выхода:");
            String city = scanner.nextLine();
            if (city.trim().equals("0")) {
                System.out.println("Досвидания ^^");
                break;
            }
            System.out.println(weatherService.generateRandomWeatherCast(city, weatherCache));
        }

    }
}