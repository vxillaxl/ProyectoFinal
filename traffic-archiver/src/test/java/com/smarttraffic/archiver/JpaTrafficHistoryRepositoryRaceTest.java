package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaTrafficHistoryRepositoryRaceTest {

    @Mock
    private TrafficReadingJpaRepository jpa;

    @Mock
    private ReadingInsert insert;

    @InjectMocks
    private JpaTrafficHistoryRepository history;

    @Test
    void unaInsercionConcurrenteRepetidaNoSeGuarda() {
        when(jpa.existsByEventId(any())).thenReturn(false);
        doThrow(new DataIntegrityViolationException("event_id")).when(insert).insert(any());

        assertFalse(history.append(new TrafficHistoryEntry(
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee"),
                new ZoneId("Zona A"),
                VehicleCount.of(8),
                Instant.parse("2026-11-09T15:00:00Z"),
                Instant.parse("2026-11-09T15:00:02Z")
        )));
    }
}
