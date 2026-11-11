package com.smarttraffic.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;

import java.time.Instant;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SensorReadingRecord(
        @JsonProperty("event_id") UUID eventId,
        @JsonProperty("zone_id") String zoneId,
        @JsonProperty("vehicle_count") int vehicleCount,
        @JsonProperty("captured_at") Instant capturedAt
) {

    public SensorReadingRecord {
        if (vehicleCount < 0) {
            throw new IllegalArgumentException("El conteo de vehículos no puede ser negativo: " + vehicleCount);
        }
    }

    public SensorReading toDomain(Instant capturedAtFallback) {
        if (eventId == null) {
            throw new IllegalArgumentException("La lectura no trae event_id.");
        }
        Instant when = capturedAt == null ? capturedAtFallback : capturedAt;
        return new SensorReading(eventId, new ZoneId(zoneId), VehicleCount.of(vehicleCount), when);
    }

    public static SensorReadingRecord from(SensorReading reading) {
        return new SensorReadingRecord(
                reading.eventId(),
                reading.zoneId().value(),
                reading.vehicleCount().value(),
                reading.capturedAt()
        );
    }
}
