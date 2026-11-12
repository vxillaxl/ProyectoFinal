package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * Fila de traffic_readings_history. El dominio ya validó el hecho.
 */
@Entity
@Table(name = "traffic_readings_history", indexes = {
        @Index(name = "idx_readings_zone", columnList = "zone_id"),
        @Index(name = "idx_readings_captured", columnList = "captured_at"),
        @Index(name = "idx_readings_archived", columnList = "archived_at")
})
public class TrafficReadingJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "zone_id", nullable = false, length = 80)
    private String zoneId;

    @Column(name = "vehicle_count", nullable = false)
    private int vehicleCount;

    @Column(name = "captured_at", nullable = false)
    private Instant capturedAt;

    @Column(name = "archived_at", nullable = false)
    private Instant archivedAt;

    protected TrafficReadingJpaEntity() {
    }

    public static TrafficReadingJpaEntity from(TrafficHistoryEntry entry) {
        TrafficReadingJpaEntity entity = new TrafficReadingJpaEntity();
        entity.eventId = entry.eventId();
        entity.zoneId = entry.zoneId().value();
        entity.vehicleCount = entry.vehicleCount().value();
        entity.capturedAt = entry.capturedAt();
        entity.archivedAt = entry.archivedAt();
        return entity;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getZoneId() {
        return zoneId;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public Instant getCapturedAt() {
        return capturedAt;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }
}
