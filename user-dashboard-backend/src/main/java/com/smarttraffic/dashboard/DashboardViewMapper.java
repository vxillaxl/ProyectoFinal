package com.smarttraffic.dashboard;

import com.smarttraffic.domain.congestion.CongestionStates;
import com.smarttraffic.messaging.CityStateRecord;
import com.smarttraffic.messaging.ZoneStateRecord;

/** Traduce el estado del bus al DTO que pinta el tablero. */
public final class DashboardViewMapper {

    private DashboardViewMapper() {
    }

    public static DashboardView toView(CityStateRecord state) {
        int[] counts = new int[3];
        for (ZoneStateRecord zone : state.zones()) {
            try {
                counts[CongestionStates.parse(zone.status()).severity()]++;
            } catch (IllegalArgumentException unknown) {
                // Un estado que no está en el catálogo no suma a ningún nivel.
            }
        }
        return new DashboardView(
                state.zones(),
                state.publishedAt(),
                new DashboardView.Summary(counts[0], counts[1], counts[2])
        );
    }
}
