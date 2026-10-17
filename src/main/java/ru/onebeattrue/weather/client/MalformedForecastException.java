package ru.onebeattrue.weather.client;

public class MalformedForecastException extends RuntimeException {

    public MalformedForecastException(String message) {
        super(message);
    }

    public MalformedForecastException(String message, Throwable cause) {
        super(message, cause);
    }
}
