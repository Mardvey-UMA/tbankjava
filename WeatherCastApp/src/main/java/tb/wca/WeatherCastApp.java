package tb.wca;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tb.wca.dto.WeatherCastDTO;
import tb.wca.exceptions.EmptyCityException;
import tb.wca.exceptions.InvalidCityException;
import tb.wca.service.interfaces.WeatherCastService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@SpringBootApplication
@RequiredArgsConstructor
public class WeatherCastApp implements CommandLineRunner {

    private final WeatherCastService weatherCastService;

    public static void main(String[] args) {
        SpringApplication.run(WeatherCastApp.class, args);
    }

    public void run(String[] args) throws EmptyCityException, InvalidCityException {

        var scanner = new Scanner(System.in);
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        while (true) {
            System.out.print("Введите город или 0 для выхода: ");
            var city = scanner.nextLine();

            if (city.trim().equals("0")) {
                System.out.println("Досвидания ^^");
                break;
            }

            WeatherCastDTO weather = weatherCastService.getWeatherCastByCityAndDate(city, date);

            System.out.println("\nПрогноз погоды для города " + city + " на " + date + ":");
            System.out.println("Температура: " + weather.getTemperature() + "C");
            System.out.println("Влажность: " + weather.getHumidity() + "%");
            System.out.println("Скорость ветра: " + weather.getWindSpeed() + " м/с");
        }
    }
}