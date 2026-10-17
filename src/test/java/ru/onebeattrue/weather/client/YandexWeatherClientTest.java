package ru.onebeattrue.weather.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.onebeattrue.weather.config.YandexWeatherClientConfig;
import ru.onebeattrue.weather.domain.Coordinates;

@RestClientTest(
        components = YandexWeatherClient.class,
        properties = {
                "yandex.weather.base-url=https://api.weather.yandex.ru",
                "yandex.weather.api-key=test-key"
        })
@Import(YandexWeatherClientConfig.class)
class YandexWeatherClientTest {

    private static final String FORECAST_URL = "https://api.weather.yandex.ru/v2/forecast?lat=55.75&lon=37.62&limit=3";

    @Autowired
    private YandexWeatherClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void sendsGetWithKeyHeaderAndReturnsBodyAsIs() {
        String body = "{\"fact\":{\"temp\":5},\"forecasts\":[]}";
        server.expect(requestTo(FORECAST_URL))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Yandex-Weather-Key", "test-key"))
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        String json = client.fetchForecastJson(new Coordinates(55.75, 37.62), 3);

        assertThat(json).isEqualTo(body);
        server.verify();
    }

    @Test
    void wrapsErrorResponseIntoWeatherApiException() {
        server.expect(requestTo(FORECAST_URL))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("{\"message\":\"forbidden\"}"));

        assertThatThrownBy(() -> client.fetchForecastJson(new Coordinates(55.75, 37.62), 3))
                .isInstanceOf(WeatherApiException.class)
                .satisfies(e -> {
                    WeatherApiException ex = (WeatherApiException) e;
                    assertThat(ex.getStatusCode().value()).isEqualTo(403);
                    assertThat(ex.getResponseBody()).contains("forbidden");
                });
    }
}
