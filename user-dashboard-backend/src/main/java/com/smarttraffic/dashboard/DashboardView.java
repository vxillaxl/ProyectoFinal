package com.smarttraffic.dashboard;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraffic.messaging.ZoneStateRecord;

import java.time.Instant;
import java.util.List;

/**
 * Lo que ve el navegador. Conserva los campos del bus y agrega el resumen.
 */
public record DashboardView(
        List<ZoneStateRecord> zones,
        @JsonProperty("published_at") Instant publishedAt,
        Summary summary
) {

    public DashboardView {
        zones = zones == null ? List.of() : List.copyOf(zones);
        summary = summary == null ? Summary.empty() : summary;
    }

    public record Summary(
            @JsonProperty("despejadas") int clear,
            @JsonProperty("moderadas") int moderate,
            @JsonProperty("congestionadas") int congested
    ) {
        public static Summary empty() {
            return new Summary(0, 0, 0);
        }
    }
}
