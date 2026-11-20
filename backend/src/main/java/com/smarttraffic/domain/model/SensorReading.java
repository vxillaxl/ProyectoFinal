package com.smarttraffic.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Hecho inmutable: un sensor observó una zona en un instante.
 * El archivador usa el eventId para no guardar dos veces la misma lectura.
 */
public record SensorReading(UUID eventId, ZoneId zoneId, VehicleCount vehicleCount, Instant capturedAt) {

    public SensorReading {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(vehicleCount, "vehicleCount");
        Objects.requireNonNull(capturedAt, "capturedAt");
    }
}
