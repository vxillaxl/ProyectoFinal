package com.smarttraffic.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smarttraffic.domain.congestion.ClearState;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageContractTest {

    private final ObjectMapper json = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    @Test
    void elJsonDelSensorUsaLosNombresDelEnunciado() throws Exception {
        UUID eventId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        SensorReadingRecord message = new SensorReadingRecord(
                eventId,
                "Zona C",
                25,
                Instant.parse("2026-11-09T12:00:00Z")
        );
        String payload = json.writeValueAsString(message);
        assertTrue(payload.contains("\"zone_id\":\"Zona C\""));
        assertTrue(payload.contains("\"vehicle_count\":25"));
        SensorReadingRecord restored = json.readValue(payload, SensorReadingRecord.class);
        assertEquals(25, restored.toDomain(Instant.EPOCH).vehicleCount().value());
    }

    @Test
    void unConteoNegativoFalla() {
        assertThrows(IllegalArgumentException.class, () -> new SensorReadingRecord(
                UUID.randomUUID(),
                "Zona A",
                -1,
                Instant.EPOCH
        ));
    }

    @Test
    void elJsonDeLaConsultaConservaUsuarioYTrayecto() throws Exception {
        QueryRequestRecord request = new QueryRequestRecord("client_456", List.of("Zona A", "Zona C"));
        String payload = json.writeValueAsString(request);
        assertTrue(payload.contains("\"user_id\":\"client_456\""));
        QueryRequestRecord restored = json.readValue(payload, QueryRequestRecord.class);
        assertEquals("answer.client_456", restored.toDomain().clientId().answerRoutingKey());
    }

    @Test
    void elEstadoDeLaZonaLlevaLasCoordenadas() throws Exception {
        ZoneSnapshot snapshot = new ZoneSnapshot(new ZoneId("Zona A"), 4.0, ClearState.INSTANCE, 2);
        ZoneStateRecord message = ZoneStateRecord.from(snapshot);
        String payload = json.writeValueAsString(message);
        assertTrue(payload.contains("\"latitude\""));
        assertTrue(payload.contains("\"longitude\""));
        ZoneStateRecord restored = json.readValue(payload, ZoneStateRecord.class);
        assertEquals(4.5981, restored.toDomain().coordinates().latitude());
        assertEquals(-74.0760, restored.toDomain().coordinates().longitude());
    }
}
