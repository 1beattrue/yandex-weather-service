package ru.onebeattrue.weather.domain;

public record Coordinates(double lat, double lon) {

    public Coordinates {
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("lat вне диапазона [-90, 90]: " + lat);
        }
        if (lon < -180 || lon > 180) {
            throw new IllegalArgumentException("lon вне диапазона [-180, 180]: " + lon);
        }
    }
}
