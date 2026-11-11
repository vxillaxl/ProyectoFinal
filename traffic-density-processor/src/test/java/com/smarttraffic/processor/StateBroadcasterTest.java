package com.smarttraffic.processor;

import com.smarttraffic.domain.congestion.CongestionStates;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import com.smarttraffic.domain.zone.CityTrafficBoard;
import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.CityStateRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StateBroadcasterTest {

    @Mock
    private CityTrafficBoard board;
    @Mock
    private RabbitTemplate rabbit;
    @Mock
    private Clock clock;
    @InjectMocks
    private StateBroadcaster broadcaster;

    @Test
    void unTableroVacioNoPublica() {
        when(board.snapshot()).thenReturn(List.of());
        broadcaster.broadcast();
        verifyNoInteractions(rabbit);
    }

    @Test
    void publicaLasZonasEnElFanout() {
        when(clock.instant()).thenReturn(Instant.EPOCH);
        when(board.snapshot()).thenReturn(List.of(
                new ZoneSnapshot(new ZoneId("Zona A"), 6, CongestionStates.parse("DESPEJADA"), 5)
        ));
        broadcaster.broadcast();
        verify(rabbit).convertAndSend(eq(BrokerNames.TRAFFIC_UPDATES), eq(""), any(CityStateRecord.class));
    }
}
