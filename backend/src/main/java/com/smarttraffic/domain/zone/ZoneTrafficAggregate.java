package com.smarttraffic.domain.zone;

import com.smarttraffic.domain.congestion.ClearState;
import com.smarttraffic.domain.congestion.CongestionState;
import com.smarttraffic.domain.density.DensityStrategy;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

/**
 * Agregado de una zona. Guarda las últimas lecturas, calcula la densidad
 * con la estrategia que recibió y cambia al registrar una lectura.
 */
public final class ZoneTrafficAggregate {

    private final ZoneId zoneId;
    private final DensityStrategy strategy;
    private final int windowSize;
    private final Deque<VehicleCount> samples;
    private CongestionState state;

    private ZoneTrafficAggregate(
            ZoneId zoneId,
            DensityStrategy strategy,
            int windowSize,
            Deque<VehicleCount> samples,
            CongestionState state
    ) {
        this.zoneId = zoneId;
        this.strategy = strategy;
        this.windowSize = windowSize;
        this.samples = samples;
        this.state = state;
    }

    public static ZoneTrafficAggregate open(ZoneId zoneId, DensityStrategy strategy, int windowSize) {
        Objects.requireNonNull(zoneId, "zoneId");
        Objects.requireNonNull(strategy, "strategy");
        if (windowSize < 1) {
            throw new IllegalArgumentException("La ventana debe contener al menos una lectura.");
        }
        return new ZoneTrafficAggregate(zoneId, strategy, windowSize, new ArrayDeque<>(), ClearState.INSTANCE);
    }

    public ZoneId zoneId() {
        return zoneId;
    }

    public synchronized CongestionTransition record(SensorReading reading) {
        Objects.requireNonNull(reading, "reading");
        if (!zoneId.equals(reading.zoneId())) {
            throw new IllegalArgumentException(
                    "La lectura pertenece a " + reading.zoneId() + " y este agregado es " + zoneId + "."
            );
        }
        samples.addLast(reading.vehicleCount());
        while (samples.size() > windowSize) {
            samples.removeFirst();
        }
        double density = strategy.calculate(List.copyOf(samples));
        CongestionState previous = state;
        state = previous.transition(density);
        return new CongestionTransition(zoneId, previous, state, density, samples.size());
    }

    public synchronized ZoneSnapshot snapshot() {
        double density = strategy.calculate(new ArrayList<>(samples));
        return new ZoneSnapshot(zoneId, density, state, samples.size());
    }
}
