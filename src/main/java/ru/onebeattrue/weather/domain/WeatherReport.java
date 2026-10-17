package ru.onebeattrue.weather.domain;

/**
 * @param rawJson               ответ сервиса без изменений
 * @param currentTemperature    текущая температура, °C (fact.temp)
 * @param averageDayTemperature среднее арифметическое parts.day.temp_avg, °C
 * @param averagedDays          за сколько суток посчитано среднее
 */
public record WeatherReport(
        String rawJson,
        int currentTemperature,
        double averageDayTemperature,
        int averagedDays
) {
}
