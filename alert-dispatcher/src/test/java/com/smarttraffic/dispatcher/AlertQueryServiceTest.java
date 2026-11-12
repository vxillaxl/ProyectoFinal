package com.smarttraffic.dispatcher;

import com.smarttraffic.domain.alert.ZoneStateReplica;
import com.smarttraffic.domain.congestion.CongestedState;
import com.smarttraffic.domain.error.ZoneNotFoundException;
import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import com.smarttraffic.domain.query.RouteAnswer;
import com.smarttraffic.domain.query.RouteQuery;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertQueryServiceTest {

    @Mock
    private ZoneStateReplica replica;

    @InjectMocks
    private AlertQueryService service;

    @Test
    void respondeSoloConLaReplicaLocal() {
        ZoneId zone = new ZoneId("Zona A");
        when(replica.view()).thenReturn(Map.of(
                zone, new ZoneSnapshot(zone, 25, CongestedState.INSTANCE, 2)
        ));

        RouteAnswer answer = service.answer(new RouteQuery(new ClientId("client_456"), List.of(zone)));

        assertEquals("CONGESTIONADA", answer.worst().label());
        assertEquals("answer.client_456", answer.clientId().answerRoutingKey());
    }

    @Test
    void unaZonaFaltanteSigueSiendoErrorDeDominio() {
        when(replica.view()).thenReturn(Map.of());
        RouteQuery query = new RouteQuery(new ClientId("client_456"), List.of(new ZoneId("Zona Z")));
        assertThrows(ZoneNotFoundException.class, () -> service.answer(query));
    }

    @Test
    void absorberReemplazaLaReplica() {
        ZoneId zone = new ZoneId("Zona B");
        List<ZoneSnapshot> snapshots = List.of(new ZoneSnapshot(zone, 3, com.smarttraffic.domain.congestion.ClearState.INSTANCE, 1));
        service.absorb(snapshots);
        verify(replica).replace(snapshots);
    }
}
