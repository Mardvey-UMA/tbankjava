package ru.doedating.service.impl;

import ru.doedating.dto.CastGeneratorDTO;
import ru.doedating.service.interfaces.CastGeneratorService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CastGeneratorServiceImpl implements CastGeneratorService {

    private final Random random = new Random();
    @Override
    public CastGeneratorDTO generateRandomCast() {
        int MIN_TEMPERATURE = -30;
        int MAX_TEMPERATURE = 30;

        int MIN_HUMIDITY = 0;
        int MAX_HUMIDITY = 100;

        int MIN_WIND_SPEED = 0;
        int MAX_WIND_SPEED  = 20;

        BigDecimal randomHumidity = BigDecimal.valueOf(MIN_HUMIDITY + (MAX_HUMIDITY - MIN_HUMIDITY) * random.nextDouble());
        BigDecimal randomTemp = BigDecimal.valueOf(MIN_TEMPERATURE + (MAX_TEMPERATURE - MIN_TEMPERATURE) * random.nextDouble());
        BigDecimal randomWindSpeed = BigDecimal.valueOf(MIN_WIND_SPEED + (MAX_WIND_SPEED - MIN_WIND_SPEED) * random.nextDouble());

        return new CastGeneratorDTO(
                randomTemp,
                randomHumidity,
                randomWindSpeed);
    }
}
