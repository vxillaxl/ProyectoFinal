package com.smarttraffic.domain.congestion;

import com.smarttraffic.domain.model.ZoneId;

/**
 * Estado de congestión de una zona. Cada nivel define a cuál puede pasar
 * y el texto de su alerta.
 */
public sealed interface CongestionState permits ClearState, ModerateState, CongestedState {

    String label();

    int severity();

    String statusPhrase();

    CongestionState transition(double density);

    default boolean outranks(CongestionState other) {
        return severity() > other.severity();
    }

    default String alert(ZoneId zoneId) {
        return "¡La " + zoneId.value() + " ahora está " + statusPhrase() + "!";
    }
}
