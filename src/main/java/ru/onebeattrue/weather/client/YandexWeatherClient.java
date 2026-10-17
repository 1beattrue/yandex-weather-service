package ru.onebeattrue.weather.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.onebeattrue.weather.domain.Coordinates;

@Component
public class YandexWeatherClient {

    private static final String FORECAST_PATH = "/v2/forecast";

    private final RestClient restClient;

    public YandexWeatherClient(@Qualifier("yandexWeatherRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * Возвращает тело ответа /v2/forecast без изменений.
     *
     * @param days количество суток прогноза (параметр limit)
     */
    public String fetchForecastJson(Coordinates coordinates, int days) {
        return restClient.get()
                .uri(uri -> uri.path(FORECAST_PATH)
                        .queryParam("lat", coordinates.lat())
                        .queryParam("lon", coordinates.lon())
                        .queryParam("limit", days)
                        .build())
                .retrieve()
                .body(String.class);
    }
}
