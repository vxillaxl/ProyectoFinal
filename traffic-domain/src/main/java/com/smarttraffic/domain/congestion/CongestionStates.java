package com.smarttraffic.domain.congestion;

import java.util.Map;

/** Reconstruye un estado a partir de la etiqueta publicada en el bus. */
public final class CongestionStates {

    private static final Map<String, CongestionState> BY_LABEL = Map.of(
            ClearState.INSTANCE.label(), ClearState.INSTANCE,
            ModerateState.INSTANCE.label(), ModerateState.INSTANCE,
            CongestedState.INSTANCE.label(), CongestedState.INSTANCE
    );

    private CongestionStates() {
    }

    public static CongestionState parse(String label) {
        CongestionState state = BY_LABEL.get(label);
        if (state == null) {
            throw new IllegalArgumentException("Estado de congestión desconocido: " + label);
        }
        return state;
    }
}
