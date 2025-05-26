package ru.doedating.service.impl;

import org.springframework.stereotype.Service;
import ru.doedating.service.interfaces.CoordinatesService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
public class CoordinatesServiceImpl implements CoordinatesService {
    @Override
    public List<BigDecimal> getCoordinatesByCityName(String cityName) {
        return Arrays.asList(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
