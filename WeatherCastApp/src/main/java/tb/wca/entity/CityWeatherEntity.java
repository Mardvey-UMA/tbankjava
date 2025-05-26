package tb.wca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "city_weather")
public class CityWeatherEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "city_id")
    private CityEntity city;

    @ManyToOne(optional = false) @JoinColumn(name = "weather_id")
    private WeatherCastEntity weather;

    private LocalDate date;
}
