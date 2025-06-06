package tb.wca.dto;

import tb.wca.model.WeatherModel;

import java.util.List;

public record WeatherResponseDTO(
        List<WeatherModel> forecasts
) { }
