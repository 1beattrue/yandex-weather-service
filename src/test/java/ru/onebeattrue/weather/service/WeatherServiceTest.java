package ru.onebeattrue.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import ru.onebeattrue.weather.client.MalformedForecastException;
import ru.onebeattrue.weather.client.YandexForecastParser;
import ru.onebeattrue.weather.client.YandexWeatherClient;
import ru.onebeattrue.weather.domain.Coordinates;
import ru.onebeattrue.weather.domain.WeatherReport;
import tools.jackson.databind.json.JsonMapper;

class WeatherServiceTest {

    private static final Coordinates MOSCOW = new Coordinates(55.75, 37.62);

    private final YandexWeatherClient client = mock(YandexWeatherClient.class);
    private final WeatherService service =
            new WeatherService(client, new YandexForecastParser(JsonMapper.builder().build()));

    @Test
    void buildsReportFromRealisticResponse() throws IOException {
        String json = readResource("/forecast-response.json");
        when(client.fetchForecastJson(MOSCOW, 2)).thenReturn(json);

        WeatherReport report = service.getReport(MOSCOW, 2);

        assertThat(report.rawJson()).isSameAs(json);
        assertThat(report.currentTemperature()).isEqualTo(6);
        assertThat(report.averageDayTemperature()).isEqualTo(6.5);
        assertThat(report.averagedDays()).isEqualTo(2);
    }

    @Test
    void averagesOnlyFirstLimitDays() {
        givenForecast(2, dayTemps(10, 20, 30));

        WeatherReport report = service.getReport(MOSCOW, 2);

        assertThat(report.averageDayTemperature()).isEqualTo(15.0);
        assertThat(report.averagedDays()).isEqualTo(2);
    }

    @Test
    void averagesAvailableDaysWhenResponseIsShorterThanLimit() {
        givenForecast(5, dayTemps(4, 8));

        WeatherReport report = service.getReport(MOSCOW, 5);

        assertThat(report.averageDayTemperature()).isEqualTo(6.0);
        assertThat(report.averagedDays()).isEqualTo(2);
    }

    @Test
    void keepsFractionalPartOfAverage() {
        givenForecast(7, dayTemps(15, 14, 9, 8, 9, 10, 9));

        WeatherReport report = service.getReport(MOSCOW, 7);

        assertThat(report.averageDayTemperature()).isCloseTo(74.0 / 7, within(1e-9));
    }

    @Test
    void handlesNegativeTemperatures() {
        givenForecast(2, dayTemps(-3, -4));

        assertThat(service.getReport(MOSCOW, 2).averageDayTemperature()).isEqualTo(-3.5);
    }

    @Test
    void failsWhenTempAvgIsMissingWithinPeriod() {
        String forecasts = "[" + day("2026-10-17", 5) + ",{\"date\":\"2026-10-18\",\"parts\":{\"day\":{}}}]";
        givenForecast(2, forecasts);

        assertThatThrownBy(() -> service.getReport(MOSCOW, 2))
                .isInstanceOf(MalformedForecastException.class)
                .hasMessageContaining("2026-10-18");
    }

    @Test
    void ignoresMissingTempAvgOutsidePeriod() {
        String forecasts = "[" + day("2026-10-17", 5) + ",{\"date\":\"2026-10-18\",\"parts\":{}}]";
        givenForecast(1, forecasts);

        assertThat(service.getReport(MOSCOW, 1).averageDayTemperature()).isEqualTo(5.0);
    }

    @Test
    void failsWhenForecastsAreMissing() {
        when(client.fetchForecastJson(MOSCOW, 3)).thenReturn("{\"fact\":{\"temp\":1}}");

        assertThatThrownBy(() -> service.getReport(MOSCOW, 3))
                .isInstanceOf(MalformedForecastException.class)
                .hasMessageContaining("forecasts");
    }

    private void givenForecast(int days, String forecastsJson) {
        when(client.fetchForecastJson(MOSCOW, days))
                .thenReturn("{\"fact\":{\"temp\":1},\"forecasts\":" + forecastsJson + "}");
    }

    private static String dayTemps(int... temps) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < temps.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(day("2026-10-" + (17 + i), temps[i]));
        }
        return sb.append(']').toString();
    }

    private static String day(String date, int tempAvg) {
        return "{\"date\":\"" + date + "\",\"parts\":{\"day\":{\"temp_avg\":" + tempAvg + "}}}";
    }

    private static String readResource(String path) throws IOException {
        try (InputStream in = WeatherServiceTest.class.getResourceAsStream(path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
