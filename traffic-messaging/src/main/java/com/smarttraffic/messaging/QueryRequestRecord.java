package com.smarttraffic.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.query.RouteQuery;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QueryRequestRecord(
        @JsonProperty("user_id") String userId,
        List<String> trayecto
) {

    public RouteQuery toDomain() {
        List<ZoneId> zones = trayecto == null
                ? List.of()
                : trayecto.stream().map(ZoneId::new).toList();
        return new RouteQuery(new ClientId(userId), zones);
    }
}
