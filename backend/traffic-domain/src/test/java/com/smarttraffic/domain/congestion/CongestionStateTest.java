package com.smarttraffic.domain.congestion;

import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CongestionStateTest {

    @Test
    void losUmbralesCoincidenConElEjemplo() {
        CongestionState clear = ClearState.INSTANCE;
        assertSame(ClearState.INSTANCE, clear.transition(9));
        assertSame(ModerateState.INSTANCE, clear.transition(10));
        assertSame(ModerateState.INSTANCE, clear.transition(24));
        assertSame(CongestedState.INSTANCE, clear.transition(25));
    }

    @Test
    void losEstadosBajanSinUnSwitchCentral() {
        assertSame(ModerateState.INSTANCE, CongestedState.INSTANCE.transition(12));
        assertSame(ClearState.INSTANCE, CongestedState.INSTANCE.transition(0));
        assertSame(CongestedState.INSTANCE, ModerateState.INSTANCE.transition(40));
    }

    @Test
    void laAlertaUsaElTextoDelEnunciado() {
        String alert = CongestedState.INSTANCE.alert(new ZoneId("Zona A"));
        assertEquals("¡La Zona A ahora está congestionada!", alert);
    }

    @Test
    void lasEtiquetasVuelvenYUnaDesconocidaFalla() {
        assertSame(CongestedState.INSTANCE, CongestionStates.parse("CONGESTIONADA"));
        assertTrue(CongestedState.INSTANCE.outranks(ModerateState.INSTANCE));
        assertThrows(IllegalArgumentException.class, () -> CongestionStates.parse("ROJA"));
    }
}
