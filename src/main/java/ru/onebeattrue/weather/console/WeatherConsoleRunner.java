package ru.onebeattrue.weather.console;

import java.util.Locale;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.onebeattrue.weather.domain.WeatherReport;
import ru.onebeattrue.weather.service.WeatherService;

@Component
@EnableConfigurationProperties(ForecastQueryProperties.class)
public class WeatherConsoleRunner implements ApplicationRunner {

    private final WeatherService weatherService;
    private final ForecastQueryProperties query;

    public WeatherConsoleRunner(WeatherService weatherService, ForecastQueryProperties query) {
        this.weatherService = weatherService;
        this.query = query;
    }

    @Override
    public void run(ApplicationArguments args) {
        WeatherReport report = weatherService.getReport(query.coordinates(), query.days());

        System.out.println("Ответ Яндекс Погоды:");
        System.out.println(report.rawJson());
        System.out.println();
        System.out.println("Текущая температура (fact.temp): " + report.currentTemperature() + " °C");
        System.out.printf(Locale.ROOT, "Средняя температура за %d сут. (parts.day.temp_avg): %.2f °C%n",
                report.averagedDays(), report.averageDayTemperature());
    }
}
