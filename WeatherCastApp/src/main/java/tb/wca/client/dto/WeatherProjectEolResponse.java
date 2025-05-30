package tb.wca.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherProjectEolResponse(
        @JsonProperty("dt_forecast") String dtForecast,
        @JsonProperty("temp_2_cel")  Double temp,
        @JsonProperty("temp_feels_cel") Double feelsLike,
        @JsonProperty("wind_speed_10")  Double windSpeed,
        @JsonProperty("wind_dir_10")    Double windDir,
        @JsonProperty("vlaga_2")        Double humidity,
        @JsonProperty("pres_surf")      Double pressure,
        @JsonProperty("uv_index")       Double uvIndex
) { }
