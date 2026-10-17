package ru.onebeattrue.weather.config;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import ru.onebeattrue.weather.client.WeatherApiException;

@Configuration
@EnableConfigurationProperties(YandexWeatherProperties.class)
public class YandexWeatherClientConfig {

    static final String API_KEY_HEADER = "X-Yandex-Weather-Key";

    @Bean
    RestClient yandexWeatherRestClient(RestClient.Builder builder, YandexWeatherProperties properties) {
        return builder
                .baseUrl(properties.baseUrl().toString())
                .defaultHeader(API_KEY_HEADER, properties.apiKey())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    throw new WeatherApiException(response.getStatusCode(), body);
                })
                .build();
    }
}
