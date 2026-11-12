package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Import({JpaTrafficHistoryRepository.class, ReadingInsert.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class JpaTrafficHistoryRepositoryTest {

    @Autowired
    private JpaTrafficHistoryRepository history;

    @Test
    void unEventoRepetidoNoSeVuelveAGuardar() {
        UUID eventId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        assertTrue(history.append(entry(eventId, "Zona A", 8)));
        assertFalse(history.append(entry(eventId, "Zona B", 30)));
    }

    @Test
    void unEventoRepetidoSeDescartaSinLanzar() {
        UUID eventId = UUID.fromString("bbbbbbbb-bbbb-cccc-dddd-eeeeeeeeeeee");
        assertTrue(history.append(entry(eventId, "Zona C", 6)));
        assertDoesNotThrow(() -> assertFalse(history.append(entry(eventId, "Zona C", 12))));
    }

    private static TrafficHistoryEntry entry(UUID eventId, String zone, int count) {
        return new TrafficHistoryEntry(
                eventId,
                new ZoneId(zone),
                VehicleCount.of(count),
                Instant.parse("2026-11-09T15:00:00Z"),
                Instant.parse("2026-11-09T15:00:02Z")
        );
    }
}
