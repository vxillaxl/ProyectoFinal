package com.smarttraffic.domain.query;

import com.smarttraffic.domain.congestion.CongestionState;
import com.smarttraffic.domain.error.ZoneNotFoundException;
import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Pregunta de un cliente por un trayecto. Se responde con la réplica
 * que el dispatcher ya tiene en memoria.
 */
public record RouteQuery(ClientId clientId, List<ZoneId> trayecto) {

    public RouteQuery {
        Objects.requireNonNull(clientId, "clientId");
        if (trayecto == null || trayecto.isEmpty()) {
            throw new IllegalArgumentException("El trayecto debe incluir al menos una zona.");
        }
        if (trayecto.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("El trayecto contiene una zona nula.");
        }
        trayecto = List.copyOf(trayecto);
    }

    public RouteAnswer answer(Map<ZoneId, ZoneSnapshot> replica) {
        Objects.requireNonNull(replica, "replica");
        List<RouteLeg> legs = new ArrayList<>();
        for (ZoneId zoneId : trayecto) {
            ZoneSnapshot snapshot = replica.get(zoneId);
            if (snapshot == null) {
                throw new ZoneNotFoundException(zoneId);
            }
            legs.add(new RouteLeg(snapshot.zoneId(), snapshot.density(), snapshot.state()));
        }
        CongestionState worst = legs.stream()
                .map(RouteLeg::state)
                .max(Comparator.comparingInt(CongestionState::severity))
                .orElseThrow();
        return new RouteAnswer(clientId, List.copyOf(legs), worst);
    }
}
