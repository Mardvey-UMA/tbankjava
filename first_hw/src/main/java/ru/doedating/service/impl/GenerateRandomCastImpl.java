package ru.doedating.service.impl;

import ru.doedating.service.interfaces.GenerateRandomCast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GenerateRandomCastImpl implements GenerateRandomCast {

    private final Random random = new Random();
    @Override
    public String generateRandomCastByCity(String city) {

        Date now = new Date();
        String dateStr = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(now);

        int MIN_TEMPERATURE = -30;
        int MAX_TEMPERATURE = 30;

        int MIN_HUMIDITY = 0;
        int MAX_HUMIDITY = 100;

        Double randomHumidity = MIN_HUMIDITY + (MAX_HUMIDITY - MIN_HUMIDITY) * random.nextDouble();
        Double randomTemp = MIN_TEMPERATURE + (MAX_TEMPERATURE - MIN_TEMPERATURE) * random.nextDouble();

        return String.format(
                "Прогноз погоды для города %s на %s \nТемпература %.2f C \nВлажность %.2f %%",
                city, dateStr, randomTemp , randomHumidity
        );
    }
}
