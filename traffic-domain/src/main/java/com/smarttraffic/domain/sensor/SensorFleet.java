package com.smarttraffic.domain.sensor;

import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

/** Flota de sensores. El azar se puede fijar en las pruebas. */
public final class SensorFleet {

    private final List<ZoneId> zones;
    private final CountSource counts;
    private final Supplier<UUID> eventIds;

    public SensorFleet(List<ZoneId> zones, IntSupplier countSupplier, Supplier<UUID> eventIds) {
        this(zones, (zone, when) -> countSupplier.getAsInt(), eventIds);
    }

    public SensorFleet(List<ZoneId> zones, CountSource counts, Supplier<UUID> eventIds) {
        if (zones == null || zones.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos una zona.");
        }
        this.zones = List.copyOf(zones);
        this.counts = Objects.requireNonNull(counts, "counts");
        this.eventIds = Objects.requireNonNull(eventIds, "eventIds");
    }

    /** Cada zona sortea su régimen. El generador se puede fijar en las pruebas. */
    public static SensorFleet regimes(List<ZoneId> zones, int maxVehicles, RandomGenerator random) {
        return new SensorFleet(zones, new RandomZoneCounts(maxVehicles, random), UUID::randomUUID);
    }

    public List<SensorReading> capture(Instant capturedAt) {
        Objects.requireNonNull(capturedAt, "capturedAt");
        List<SensorReading> readings = new ArrayList<>();
        for (ZoneId zone : zones) {
            int count = counts.count(zone, capturedAt);
            readings.add(new SensorReading(eventIds.get(), zone, VehicleCount.of(count), capturedAt));
        }
        return List.copyOf(readings);
    }

    @FunctionalInterface
    public interface CountSource {
        int count(ZoneId zone, Instant when);
    }
}
