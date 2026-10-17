package ru.onebeattrue.weather.client;

import org.springframework.stereotype.Component;
import ru.onebeattrue.weather.client.dto.YandexForecastResponse;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class YandexForecastParser {

    private final JsonMapper jsonMapper;

    public YandexForecastParser(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public YandexForecastResponse parse(String json) {
        YandexForecastResponse response;
        try {
            response = jsonMapper.readValue(json, YandexForecastResponse.class);
        } catch (JacksonException e) {
            throw new MalformedForecastException("Не удалось разобрать ответ Яндекс Погоды", e);
        }
        if (response == null || response.fact() == null || response.fact().temp() == null) {
            throw new MalformedForecastException("В ответе Яндекс Погоды нет поля fact.temp");
        }
        return response;
    }
}
