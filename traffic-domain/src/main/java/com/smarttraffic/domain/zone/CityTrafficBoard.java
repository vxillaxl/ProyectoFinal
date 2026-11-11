package com.smarttraffic.domain.zone;

import com.smarttraffic.domain.density.DensityStrategy;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.ZoneCatalog;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Estado vigente de la ciudad. La clave es la zona y el valor es su agregado.
 * Cada lectura nueva actualiza solo esa zona.
 */
public final class CityTrafficBoard {

    private final Map<ZoneId, ZoneTrafficAggregate> zoneStates = new LinkedHashMap<>();
    private final DensityStrategy strategy;
    private final int windowSize;

    public CityTrafficBoard(DensityStrategy strategy, int windowSize) {
        this.strategy = Objects.requireNonNull(strategy, "strategy");
        if (windowSize < 1) {
            throw new IllegalArgumentException("La ventana debe contener al menos una lectura.");
        }
        this.windowSize = windowSize;
    }

    public synchronized CongestionTransition ingest(SensorReading reading) {
        Objects.requireNonNull(reading, "reading");
        ZoneCatalog.require(reading.zoneId());
        ZoneTrafficAggregate aggregate = zoneStates.computeIfAbsent(
                reading.zoneId(),
                id -> ZoneTrafficAggregate.open(id, strategy, windowSize)
        );
        return aggregate.record(reading);
    }

    public synchronized List<ZoneSnapshot> snapshot() {
        return zoneStates.values().stream()
                .map(ZoneTrafficAggregate::snapshot)
                .sorted(Comparator.comparing(item -> item.zoneId().value()))
                .toList();
    }
}
