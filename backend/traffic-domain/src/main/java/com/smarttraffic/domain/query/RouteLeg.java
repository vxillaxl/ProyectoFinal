package com.smarttraffic.domain.query;

import com.smarttraffic.domain.congestion.CongestionState;
import com.smarttraffic.domain.model.ZoneId;

public record RouteLeg(ZoneId zoneId, double density, CongestionState state) {

    public RouteLeg {
        if (zoneId == null || state == null) {
            throw new IllegalArgumentException("Un tramo necesita zona y estado.");
        }
        if (Double.isNaN(density) || density < 0) {
            throw new IllegalArgumentException("La densidad del tramo no es válida.");
        }
    }
}
