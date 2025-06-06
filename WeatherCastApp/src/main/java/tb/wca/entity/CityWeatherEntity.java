package tb.wca.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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

    private LocalDate date;

    private LocalTime time;

    @ManyToOne(optional = false) @JoinColumn(name = "city_id")
    private CityEntity city;

    @ManyToOne(optional = false) @JoinColumn(name = "weather_id")
    private WeatherCastEntity weather;
}
