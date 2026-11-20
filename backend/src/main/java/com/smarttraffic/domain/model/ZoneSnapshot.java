package com.smarttraffic.domain.model;

import com.smarttraffic.domain.congestion.CongestionState;

import java.util.Objects;

/** Fotografía inmutable del estado actual de una zona. */
public record ZoneSnapshot(
        ZoneId zoneId,
        double density,
        CongestionState state,
        int sampleCount,
        Coordinates coordinates
) {

    public ZoneSnapshot(ZoneId zoneId, double density, CongestionState state, int sampleCount) {
        this(zoneId, density, state, sampleCount, ZoneCatalog.require(zoneId));
    }

    public ZoneSnapshot {
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(coordinates, "coordinates");
        if (Double.isNaN(density) || density < 0) {
            throw new IllegalArgumentException("La densidad debe ser un número mayor o igual a cero.");
        }
        if (sampleCount < 0) {
            throw new IllegalArgumentException("La cantidad de muestras no puede ser negativa.");
        }
    }
}
