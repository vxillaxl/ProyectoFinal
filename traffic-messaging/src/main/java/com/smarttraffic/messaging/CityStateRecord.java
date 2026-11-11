package com.smarttraffic.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraffic.domain.model.ZoneSnapshot;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CityStateRecord(
        List<ZoneStateRecord> zones,
        @JsonProperty("published_at") Instant publishedAt
) {

    public CityStateRecord {
        zones = zones == null ? List.of() : List.copyOf(zones);
    }

    public List<com.smarttraffic.domain.model.ZoneSnapshot> toDomain() {
        return zones.stream().map(ZoneStateRecord::toDomain).toList();
    }

    public static CityStateRecord from(List<ZoneSnapshot> snapshots, Instant publishedAt) {
        return new CityStateRecord(
                snapshots.stream().map(ZoneStateRecord::from).toList(),
                publishedAt
        );
    }
}
