package tb.wca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tb.wca.entity.WeatherCastEntity;

import java.util.List;

public interface WeatherCastRepository extends JpaRepository<WeatherCastEntity, Long> {
}
