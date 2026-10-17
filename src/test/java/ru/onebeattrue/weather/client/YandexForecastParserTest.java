package ru.onebeattrue.weather.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import ru.onebeattrue.weather.client.dto.YandexForecastResponse;
import tools.jackson.databind.json.JsonMapper;

class YandexForecastParserTest {

    private final YandexForecastParser parser = new YandexForecastParser(JsonMapper.builder().build());

    @Test
    void parsesFactTempIgnoringUnknownFields() throws IOException {
        YandexForecastResponse response = parser.parse(readResource("/forecast-response.json"));

        assertThat(response.fact().temp()).isEqualTo(6);
        assertThat(response.forecasts())
                .extracting(day -> day.parts().day().tempAvg())
                .containsExactly(7, 6);
    }

    @Test
    void parsesNegativeTemperature() {
        YandexForecastResponse response = parser.parse("{\"fact\":{\"temp\":-12}}");

        assertThat(response.fact().temp()).isEqualTo(-12);
    }

    @Test
    void failsWhenFactIsMissing() {
        assertThatThrownBy(() -> parser.parse("{\"forecasts\":[]}"))
                .isInstanceOf(MalformedForecastException.class)
                .hasMessageContaining("fact.temp");
    }

    @Test
    void failsWhenTempIsMissing() {
        assertThatThrownBy(() -> parser.parse("{\"fact\":{\"condition\":\"clear\"}}"))
                .isInstanceOf(MalformedForecastException.class)
                .hasMessageContaining("fact.temp");
    }

    @Test
    void failsOnInvalidJson() {
        assertThatThrownBy(() -> parser.parse("not a json"))
                .isInstanceOf(MalformedForecastException.class);
    }

    private static String readResource(String path) throws IOException {
        try (InputStream in = YandexForecastParserTest.class.getResourceAsStream(path)) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
