package ru.onebeattrue.weather.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.onebeattrue.weather.client.MalformedForecastException;
import ru.onebeattrue.weather.client.YandexForecastParser;
import ru.onebeattrue.weather.client.YandexWeatherClient;
import ru.onebeattrue.weather.client.dto.YandexForecastResponse;
import ru.onebeattrue.weather.client.dto.YandexForecastResponse.DayForecast;
import ru.onebeattrue.weather.domain.Coordinates;
import ru.onebeattrue.weather.domain.WeatherReport;

@Service
public class WeatherService {

    private static final Logger log = LoggerFactory.getLogger(WeatherService.class);

    private final YandexWeatherClient client;
    private final YandexForecastParser parser;

    public WeatherService(YandexWeatherClient client, YandexForecastParser parser) {
        this.client = client;
        this.parser = parser;
    }

    public WeatherReport getReport(Coordinates coordinates, int days) {
        String json = client.fetchForecastJson(coordinates, days);
        YandexForecastResponse response = parser.parse(json);

        List<DayForecast> period = firstDays(response.forecasts(), days);
        double average = averageDayTemperature(period);

        return new WeatherReport(json, response.fact().temp(), average, period.size());
    }

    private static List<DayForecast> firstDays(List<DayForecast> forecasts, int days) {
        if (forecasts.isEmpty()) {
            throw new MalformedForecastException("В ответе Яндекс Погоды нет forecasts");
        }
        if (forecasts.size() < days) {
            log.warn("Запрошено суток: {}, в ответе: {}. Среднее посчитано по доступным", days, forecasts.size());
        }
        return forecasts.subList(0, Math.min(days, forecasts.size()));
    }

    private static double averageDayTemperature(List<DayForecast> period) {
        return period.stream()
                .mapToInt(WeatherService::dayTempAvg)
                .average()
                .orElseThrow();
    }

    private static int dayTempAvg(DayForecast forecast) {
        if (forecast == null || forecast.parts() == null || forecast.parts().day() == null
                || forecast.parts().day().tempAvg() == null) {
            String date = forecast == null ? "?" : forecast.date();
            throw new MalformedForecastException("В прогнозе на " + date + " нет parts.day.temp_avg");
        }
        return forecast.parts().day().tempAvg();
    }
}
