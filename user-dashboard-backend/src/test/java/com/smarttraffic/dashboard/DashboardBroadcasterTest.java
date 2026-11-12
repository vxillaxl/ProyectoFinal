package com.smarttraffic.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardBroadcasterTest {

    @Mock
    private WebSocketSession session;

    private DashboardBroadcaster broadcaster;

    @BeforeEach
    void setUp() {
        ObjectMapper json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        broadcaster = new DashboardBroadcaster(json);
    }

    @Test
    void unaSesionNuevaRecibeElUltimoEstado() throws Exception {
        when(session.isOpen()).thenReturn(true);
        broadcaster.afterConnectionEstablished(session);
        verify(session).sendMessage(any());
    }

    @Test
    void unaSesionCerradaSeDescarta() throws Exception {
        when(session.isOpen()).thenReturn(true);
        broadcaster.afterConnectionEstablished(session);
        broadcaster.afterConnectionClosed(session, null);
        broadcaster.publish(new DashboardView(List.of(), Instant.EPOCH, DashboardView.Summary.empty()));
        verify(session, times(1)).sendMessage(any());
    }
}
