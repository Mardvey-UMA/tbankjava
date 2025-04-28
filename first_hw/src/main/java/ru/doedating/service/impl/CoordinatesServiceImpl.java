package ru.doedating.service.impl;

import org.antlr.v4.runtime.misc.Pair;
import ru.doedating.service.interfaces.CoordinatesService;

import java.math.BigDecimal;

public class CoordinatesServiceImpl implements CoordinatesService {
    @Override
    public Pair<BigDecimal, BigDecimal> getCoordinatesByCityName(String cityName) {
        return new Pair<>(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
