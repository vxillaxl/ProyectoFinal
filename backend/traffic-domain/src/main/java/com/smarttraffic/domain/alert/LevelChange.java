package com.smarttraffic.domain.alert;

import com.smarttraffic.domain.model.ZoneSnapshot;

import java.util.Objects;

/** Subida de nivel detectada al comparar la réplica anterior con la nueva. */
public record LevelChange(ZoneSnapshot previous, ZoneSnapshot current) {

    public LevelChange {
        Objects.requireNonNull(previous, "previous");
        Objects.requireNonNull(current, "current");
        if (!previous.zoneId().equals(current.zoneId())) {
            throw new IllegalArgumentException("El cambio debe referirse a la misma zona.");
        }
    }
}
