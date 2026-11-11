package com.smarttraffic.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smarttraffic.domain.congestion.CongestionStates;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.zone.CityTrafficBoard;
import com.smarttraffic.domain.zone.CongestionTransition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingListenerTest {

    @Mock
    private CityTrafficBoard board;
    @Mock
    private Clock clock;

    private ReadingListener listener;

    @BeforeEach
    void setUp() {
        ObjectMapper json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        listener = new ReadingListener(board, json, clock);
    }

    @Test
    void unMensajeValidoSeIncorpora() throws Exception {
        when(clock.instant()).thenReturn(Instant.parse("2026-11-09T12:00:00Z"));
        when(board.ingest(any())).thenReturn(new CongestionTransition(
                new ZoneId("Zona A"),
                CongestionStates.parse("DESPEJADA"),
                CongestionStates.parse("DESPEJADA"),
                4,
                1
        ));
        listener.onReading("""
                {"event_id":"aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee","zone_id":"Zona A","vehicle_count":4,"captured_at":"2026-11-09T12:00:00Z"}
                """);
        verify(board).ingest(any());
    }

    @Test
    void unMensajeInvalidoFalla() {
        assertThrows(JsonProcessingException.class, () -> listener.onReading("esto-no-es-json"));
    }
}
