package com.smarttraffic.domain.model;

/**
 * Punto geográfico. La latitud va de -90 a 90 y la longitud de -180 a 180.
 */
public record Coordinates(double latitude, double longitude) {

    public Coordinates {
        if (!inRange(latitude, -90, 90) || !inRange(longitude, -180, 180)) {
            throw new IllegalArgumentException("Las coordenadas están fuera de rango.");
        }
    }

    private static boolean inRange(double value, double min, double max) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && value >= min && value <= max;
    }
}
