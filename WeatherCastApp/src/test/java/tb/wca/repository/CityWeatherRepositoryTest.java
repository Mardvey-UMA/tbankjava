package tb.wca.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import tb.wca.AbstractContainerBaseTest;
import tb.wca.entity.CityWeatherEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CityWeatherRepositoryTest extends AbstractContainerBaseTest {

    @Autowired
    private CityWeatherRepository cityWeatherRepository;

    @AfterEach
    void shutDown() {
        cityWeatherRepository.deleteAll();
    }

    @Test
    void findByCityNameAndDate_shouldReturnRecords() {
        List<CityWeatherEntity> results = cityWeatherRepository.findByCity_NameAndDate(
                "Обнинск", LocalDate.of(2025, 12, 1));

        assertEquals(2, results.size());
    }

    @Test
    void findByCityNameAndDateAndTime_shouldReturnSingleRecord() {
        Optional<CityWeatherEntity> result = cityWeatherRepository.findByCity_NameAndDateAndTime(
                "Обнинск",
                LocalDate.of(2025, 12, 1),
                LocalTime.of(6, 0)
        );
        assertTrue(result.isPresent());
        assertEquals(LocalDate.of(2025, 12, 1), result.get().getDate());
        assertEquals(LocalTime.of(6, 0), result.get().getTime());
        assertEquals("Обнинск", result.get().getCity().getName());
    }

    @Test
    void findByCityNameAndDateBetween_shouldReturnRange() {
        List<CityWeatherEntity> results = cityWeatherRepository.findByCity_NameAndDateBetween(
                "Саратов",
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 5, 31)
        );

        assertEquals(2, results.size());
    }
}