package com.smarttraffic.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraffic.domain.congestion.CongestionStates;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ZoneStateRecord(
        @JsonProperty("zone_id") String zoneId,
        double density,
        String status,
        @JsonProperty("sample_count") int sampleCount,
        double latitude,
        double longitude
) {

    public ZoneSnapshot toDomain() {
        return new ZoneSnapshot(
                new ZoneId(zoneId),
                density,
                CongestionStates.parse(status),
                sampleCount,
                new com.smarttraffic.domain.model.Coordinates(latitude, longitude)
        );
    }

    public static ZoneStateRecord from(ZoneSnapshot snapshot) {
        return new ZoneStateRecord(
                snapshot.zoneId().value(),
                snapshot.density(),
                snapshot.state().label(),
                snapshot.sampleCount(),
                snapshot.coordinates().latitude(),
                snapshot.coordinates().longitude()
        );
    }
}
