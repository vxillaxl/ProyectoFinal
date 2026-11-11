package com.smarttraffic.domain.query;

import com.smarttraffic.domain.congestion.CongestionState;
import com.smarttraffic.domain.model.ClientId;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public record RouteAnswer(ClientId clientId, List<RouteLeg> legs, CongestionState worst) {

    public RouteAnswer {
        Objects.requireNonNull(clientId, "clientId");
        Objects.requireNonNull(worst, "worst");
        if (legs == null || legs.isEmpty()) {
            throw new IllegalArgumentException("La respuesta debe describir al menos un tramo.");
        }
        legs = List.copyOf(legs);
    }

    public String summary() {
        String detail = legs.stream()
                .map(leg -> leg.zoneId() + " " + leg.state().label()
                        + " (" + String.format(Locale.US, "%.1f", leg.density()) + ")")
                .collect(Collectors.joining(", "));
        return "Trayecto de " + clientId.value() + ": " + detail + ". Peor tramo: " + worst.label() + ".";
    }
}
