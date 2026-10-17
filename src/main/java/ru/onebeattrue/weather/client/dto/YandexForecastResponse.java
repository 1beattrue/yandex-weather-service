package ru.onebeattrue.weather.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record YandexForecastResponse(Fact fact, List<DayForecast> forecasts) {

    public YandexForecastResponse {
        forecasts = forecasts == null ? List.of() : List.copyOf(forecasts);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Fact(Integer temp) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DayForecast(String date, Parts parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Parts(Part day) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(@JsonProperty("temp_avg") Integer tempAvg) {
    }
}
