package ru.doedating.service.interfaces;
import java.math.BigDecimal;
import java.util.List;

public interface CoordinatesService {
    List<BigDecimal> getCoordinatesByCityName(String cityName);
}
