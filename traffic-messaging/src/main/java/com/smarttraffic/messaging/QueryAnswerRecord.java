package com.smarttraffic.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.smarttraffic.domain.query.RouteAnswer;
import com.smarttraffic.domain.query.RouteLeg;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QueryAnswerRecord(
        @JsonProperty("user_id") String userId,
        List<LegRecord> trayecto,
        @JsonProperty("worst_status") String worstStatus,
        String summary,
        String error
) {

    public QueryAnswerRecord {
        trayecto = trayecto == null ? List.of() : List.copyOf(trayecto);
    }

    public static QueryAnswerRecord from(RouteAnswer answer) {
        List<LegRecord> legs = answer.legs().stream().map(QueryAnswerRecord::leg).toList();
        return new QueryAnswerRecord(
                answer.clientId().value(),
                legs,
                answer.worst().label(),
                answer.summary(),
                null
        );
    }

    public static QueryAnswerRecord failure(String userId, String error) {
        return new QueryAnswerRecord(userId, List.of(), null, error, error);
    }

    private static LegRecord leg(RouteLeg leg) {
        return new LegRecord(leg.zoneId().value(), leg.density(), leg.state().label());
    }
}
