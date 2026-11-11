package com.smarttraffic.domain.query;

import com.smarttraffic.domain.congestion.ClearState;
import com.smarttraffic.domain.congestion.CongestedState;
import com.smarttraffic.domain.congestion.ModerateState;
import com.smarttraffic.domain.error.ZoneNotFoundException;
import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteQueryTest {

    private final ClientId client = new ClientId("client_456");
    private final ZoneId zoneA = new ZoneId("Zona A");
    private final ZoneId zoneC = new ZoneId("Zona C");

    @Test
    void elPeorTramoDefineElTrayecto() {
        RouteQuery query = new RouteQuery(client, List.of(zoneA, zoneC));
        RouteAnswer answer = query.answer(Map.of(
                zoneA, new ZoneSnapshot(zoneA, 4, ClearState.INSTANCE, 3),
                zoneC, new ZoneSnapshot(zoneC, 25, CongestedState.INSTANCE, 3)
        ));
        assertSame(CongestedState.INSTANCE, answer.worst());
        assertTrue(answer.summary().contains("Peor tramo: CONGESTIONADA"));
        assertEquals(2, answer.legs().size());
    }

    @Test
    void unaZonaFaltanteEsErrorDeDominio() {
        RouteQuery query = new RouteQuery(client, List.of(zoneA, zoneC));
        ZoneNotFoundException error = assertThrows(ZoneNotFoundException.class, () -> query.answer(Map.of(
                zoneA, new ZoneSnapshot(zoneA, 11, ModerateState.INSTANCE, 2)
        )));
        assertEquals(zoneC, error.zoneId());
    }

    @Test
    void unTrayectoVacioSeRechaza() {
        assertThrows(IllegalArgumentException.class, () -> new RouteQuery(client, List.of()));
    }
}
