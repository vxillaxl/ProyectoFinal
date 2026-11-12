package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import com.smarttraffic.domain.history.TrafficHistoryRepository;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficArchiverServiceTest {

    @Mock
    private TrafficHistoryRepository history;

    @Mock
    private Clock clock;

    @InjectMocks
    private TrafficArchiverService service;

    @Test
    void guardaLaLecturaPorElRepositorio() {
        Instant archivedAt = Instant.parse("2026-11-09T12:00:02Z");
        when(clock.instant()).thenReturn(archivedAt);
        when(history.append(any())).thenReturn(true);
        SensorReading reading = reading();

        assertTrue(service.archive(reading));

        ArgumentCaptor<TrafficHistoryEntry> captor = ArgumentCaptor.forClass(TrafficHistoryEntry.class);
        verify(history).append(captor.capture());
        assertEquals(reading.eventId(), captor.getValue().eventId());
        assertEquals("Zona C", captor.getValue().zoneId().value());
        assertEquals(25, captor.getValue().vehicleCount().value());
        assertEquals(archivedAt, captor.getValue().archivedAt());
    }

    @Test
    void avisaSiElEventoYaEstabaGuardado() {
        when(clock.instant()).thenReturn(Instant.EPOCH);
        when(history.append(any())).thenReturn(false);
        assertFalse(service.archive(reading()));
    }

    private static SensorReading reading() {
        return new SensorReading(
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                new ZoneId("Zona C"),
                VehicleCount.of(25),
                Instant.parse("2026-11-09T12:00:00Z")
        );
    }
}
