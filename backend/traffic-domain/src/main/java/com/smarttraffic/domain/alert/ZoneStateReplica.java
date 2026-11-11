package com.smarttraffic.domain.alert;

import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Copia local del estado publicado por el procesador.
 * El dispatcher responde consultas desde aquí y avisa a sus observers
 * solo cuando una zona empeora.
 */
public final class ZoneStateReplica {

    private final Map<ZoneId, ZoneSnapshot> zoneStates = new LinkedHashMap<>();
    private final List<CongestionObserver> observers;

    public ZoneStateReplica(List<CongestionObserver> observers) {
        this.observers = List.copyOf(Objects.requireNonNull(observers, "observers"));
    }

    public synchronized List<LevelChange> replace(List<ZoneSnapshot> published) {
        Objects.requireNonNull(published, "published");
        List<LevelChange> raised = new ArrayList<>();
        for (ZoneSnapshot current : published) {
            ZoneSnapshot previous = zoneStates.put(current.zoneId(), current);
            if (previous != null && current.state().outranks(previous.state())) {
                LevelChange change = new LevelChange(previous, current);
                raised.add(change);
                for (CongestionObserver observer : observers) {
                    observer.onLevelRaised(change);
                }
            }
        }
        return List.copyOf(raised);
    }

    public synchronized Map<ZoneId, ZoneSnapshot> view() {
        return Map.copyOf(zoneStates);
    }
}
