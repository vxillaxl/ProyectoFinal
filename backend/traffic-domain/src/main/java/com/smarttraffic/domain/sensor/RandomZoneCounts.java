package com.smarttraffic.domain.sensor;

import com.smarttraffic.domain.model.ZoneId;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * Cada zona sortea un régimen y se queda en él entre 20 y 60 segundos.
 * Cada lectura es un entero al azar dentro del rango de ese régimen.
 */
public final class RandomZoneCounts implements SensorFleet.CountSource {

    static final int MIN_VEHICLES = 3;
    private static final int MIN_REGIME_SECONDS = 20;
    private static final int REGIME_SPAN_SECONDS = 41;

    private static final List<Band> BANDS = List.of(
            new Band(3, 9),
            new Band(10, 24),
            new Band(25, 40)
    );

    private final int maxVehicles;
    private final RandomGenerator random;
    private final Map<ZoneId, Hold> holds = new HashMap<>();

    public RandomZoneCounts(int maxVehicles, RandomGenerator random) {
        if (maxVehicles < 2) {
            throw new IllegalArgumentException("El tope del conteo debe ser positivo.");
        }
        this.maxVehicles = maxVehicles;
        this.random = Objects.requireNonNull(random, "random");
    }

    @Override
    public int count(ZoneId zone, Instant when) {
        Objects.requireNonNull(zone, "zone");
        Objects.requireNonNull(when, "when");
        Hold hold = holds.get(zone);
        if (hold == null || !when.isBefore(hold.until)) {
            Band band = BANDS.get(random.nextInt(BANDS.size()));
            int seconds = MIN_REGIME_SECONDS + random.nextInt(REGIME_SPAN_SECONDS);
            hold = new Hold(band, when.plusSeconds(seconds));
            holds.put(zone, hold);
        }
        int cap = maxVehicles - 1;
        int floor = Math.min(MIN_VEHICLES, cap);
        int low = Math.max(Math.min(hold.band.low, cap), floor);
        int high = Math.max(Math.min(hold.band.high, cap), low);
        return low + random.nextInt(high - low + 1);
    }

    private record Band(int low, int high) {
    }

    private record Hold(Band band, Instant until) {
    }
}
