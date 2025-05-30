package tb.wca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class WeatherCastApp{
    public static void main(String[] args) {
        SpringApplication.run(WeatherCastApp.class, args);
    }
}