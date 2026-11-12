package com.smarttraffic.dispatcher;

import com.smarttraffic.domain.congestion.CongestionStates;
import com.smarttraffic.domain.error.ZoneNotFoundException;
import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.query.RouteAnswer;
import com.smarttraffic.domain.query.RouteLeg;
import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.CityStateRecord;
import com.smarttraffic.messaging.QueryAnswerRecord;
import com.smarttraffic.messaging.QueryRequestRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispatchListenersTest {

    @Mock
    private AlertQueryService service;
    @Mock
    private RabbitTemplate rabbit;
    @InjectMocks
    private DispatchListeners listeners;

    @Test
    void unaConsultaValidaRespondeEnLaClaveDelCliente() {
        when(service.answer(any())).thenReturn(new RouteAnswer(
                new ClientId("client_456"),
                List.of(new RouteLeg(new ZoneId("Zona A"), 4.0, CongestionStates.parse("DESPEJADA"))),
                CongestionStates.parse("DESPEJADA")
        ));
        listeners.onQuery(new QueryRequestRecord("client_456", List.of("Zona A")));
        verify(rabbit).convertAndSend(eq(BrokerNames.QUERY_ANSWERS), eq("answer.client_456"), any(QueryAnswerRecord.class));
    }

    @Test
    void unaZonaDesconocidaRespondeErrorAEseCliente() {
        when(service.answer(any())).thenThrow(new ZoneNotFoundException(new ZoneId("Zona Z")));
        listeners.onQuery(new QueryRequestRecord("client_456", List.of("Zona Z")));
        ArgumentCaptor<QueryAnswerRecord> answer = ArgumentCaptor.forClass(QueryAnswerRecord.class);
        verify(rabbit).convertAndSend(eq(BrokerNames.QUERY_ANSWERS), eq("answer.client_456"), answer.capture());
        assertNotNull(answer.getValue().error());
    }

    @Test
    void elEstadoDelFanoutSeIncorpora() {
        CityStateRecord state = new CityStateRecord(List.of(), Instant.EPOCH);
        listeners.onState(state);
        verify(service).absorb(state.toDomain());
    }
}
