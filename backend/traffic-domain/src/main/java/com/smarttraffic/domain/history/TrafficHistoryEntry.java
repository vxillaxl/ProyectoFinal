package com.smarttraffic.domain.history;

import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Hecho del histórico, armado a partir de una lectura ya validada.
 */
public record TrafficHistoryEntry(
        UUID eventId,
        ZoneId zoneId,
        VehicleCount vehicleCount,
        Instant capturedAt,
        Instant archivedAt
) {

    public TrafficHistoryEntry {
        Objects.requireNonNull(eventId, "eventId");
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(vehicleCount, "vehicleCount");
        Objects.requireNonNull(capturedAt, "capturedAt");
        Objects.requireNonNull(archivedAt, "archivedAt");
    }

    public static TrafficHistoryEntry capture(SensorReading reading, Instant archivedAt) {
        return new TrafficHistoryEntry(
                reading.eventId(),
                reading.zoneId(),
                reading.vehicleCount(),
                reading.capturedAt(),
                archivedAt
        );
    }
}
