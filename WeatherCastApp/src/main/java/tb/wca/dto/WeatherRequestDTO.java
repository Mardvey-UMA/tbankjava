package tb.wca.dto;

public record WeatherRequestDTO(
        String cityName,
        String date,
        String startDate,
        String endDate,
        String hour
) { }
