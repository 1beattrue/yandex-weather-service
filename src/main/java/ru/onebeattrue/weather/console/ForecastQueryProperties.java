package ru.onebeattrue.weather.console;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import ru.onebeattrue.weather.domain.Coordinates;

@Validated
@ConfigurationProperties(prefix = "weather.query")
public record ForecastQueryProperties(
        @DecimalMin("-90") @DecimalMax("90") double lat,
        @DecimalMin("-180") @DecimalMax("180") double lon,
        @Min(1) int days
) {

    public Coordinates coordinates() {
        return new Coordinates(lat, lon);
    }
}
