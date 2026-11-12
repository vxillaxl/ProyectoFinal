package com.smarttraffic.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class DashboardBroadcaster extends TextWebSocketHandler {

    private final ObjectMapper json;
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private volatile String latest = "{\"zones\":[]}";

    public DashboardBroadcaster(ObjectMapper json) {
        this.json = json;
    }

    public void publish(DashboardView view) {
        try {
            latest = json.writeValueAsString(view);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar el estado de la ciudad", exception);
        }
        TextMessage message = new TextMessage(latest);
        for (WebSocketSession session : sessions) {
            send(session, message);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        send(session, new TextMessage(latest));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    private void send(WebSocketSession session, TextMessage message) {
        synchronized (session) {
            if (!session.isOpen()) {
                sessions.remove(session);
                return;
            }
            try {
                session.sendMessage(message);
            } catch (IOException exception) {
                sessions.remove(session);
            }
        }
    }
}
