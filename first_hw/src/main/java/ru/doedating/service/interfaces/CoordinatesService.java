package ru.doedating.service.interfaces;

import org.antlr.v4.runtime.misc.Pair;

import java.math.BigDecimal;

public interface CoordinatesService {
    Pair<BigDecimal, BigDecimal> getCoordinatesByCityName(String cityName);
}
