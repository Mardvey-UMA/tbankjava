package tb.wca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Builder
@Table(name = "weather")
@NoArgsConstructor
@AllArgsConstructor
public class WeatherCastEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal temperature;
    private BigDecimal humidity;

    @Column(name = "wind_speed")
    private BigDecimal windSpeed;
}
