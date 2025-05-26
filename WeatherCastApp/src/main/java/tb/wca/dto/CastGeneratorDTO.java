package tb.wca.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@RequiredArgsConstructor
public class CastGeneratorDTO {
    BigDecimal temperature;
    BigDecimal humidity;
    BigDecimal windSpeed;

    public CastGeneratorDTO(BigDecimal randomTemp, BigDecimal randomHumidity, BigDecimal randomWindSpeed) {
        this.temperature = randomTemp;
        this.humidity = randomHumidity;
        this.windSpeed = randomWindSpeed;
    }
}
