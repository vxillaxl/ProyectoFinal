package com.smarttraffic.domain.zone;

import com.smarttraffic.domain.congestion.CongestionState;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;

/**
 * Resultado de incorporar una lectura. Dice si el nivel subió,
 * sin obligar al llamador a comparar estados a mano.
 */
public record CongestionTransition(
        ZoneId zoneId,
        CongestionState previous,
        CongestionState current,
        double density,
        int sampleCount
) {

    public CongestionTransition {
        if (zoneId == null || previous == null || current == null) {
            throw new IllegalArgumentException("La transición necesita zona y estados.");
        }
        if (Double.isNaN(density) || density < 0 || sampleCount < 0) {
            throw new IllegalArgumentException("La transición tiene densidad o muestras inválidas.");
        }
    }

    public boolean levelRaised() {
        return current.outranks(previous);
    }

    public ZoneSnapshot snapshot() {
        return new ZoneSnapshot(zoneId, density, current, sampleCount);
    }
}
