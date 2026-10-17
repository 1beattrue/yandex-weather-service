package ru.onebeattrue.weather.config;

import java.net.URI;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "yandex.weather")
public record YandexWeatherProperties(
        @NotNull URI baseUrl,
        @NotBlank String apiKey
) {
}
