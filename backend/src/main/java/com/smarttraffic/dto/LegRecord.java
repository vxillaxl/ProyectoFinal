package com.smarttraffic.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LegRecord(
        @JsonProperty("zone_id") String zoneId,
        double density,
        String status
) {
}
