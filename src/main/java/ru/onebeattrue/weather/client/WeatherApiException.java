package ru.onebeattrue.weather.client;

import org.springframework.http.HttpStatusCode;

public class WeatherApiException extends RuntimeException {

    private final HttpStatusCode statusCode;
    private final String responseBody;

    public WeatherApiException(HttpStatusCode statusCode, String responseBody) {
        super("Yandex Weather API вернул " + statusCode.value() + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
