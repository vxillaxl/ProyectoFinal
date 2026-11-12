package com.smarttraffic.dashboard;

import com.smarttraffic.messaging.CityStateRecord;
import com.smarttraffic.messaging.ZoneStateRecord;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DashboardViewMapperTest {

    @Test
    void conservaLasZonasYCuentaCadaNivel() {
        CityStateRecord state = new CityStateRecord(List.of(
                new ZoneStateRecord("Zona A", 4.0, "DESPEJADA", 5, 4.5981, -74.0760),
                new ZoneStateRecord("Zona B", 30.0, "CONGESTIONADA", 5, 4.6486, -74.0628),
                new ZoneStateRecord("Zona C", 12.0, "MODERADA", 5, 4.6290, -74.1515)
        ), Instant.parse("2026-11-11T15:00:00Z"));
        DashboardView view = DashboardViewMapper.toView(state);
        assertEquals("Zona A", view.zones().get(0).zoneId());
        assertEquals(4.0, view.zones().get(0).density());
        assertEquals("DESPEJADA", view.zones().get(0).status());
        assertEquals(5, view.zones().get(0).sampleCount());
        assertEquals(4.5981, view.zones().get(0).latitude());
        assertEquals(-74.0760, view.zones().get(0).longitude());
        assertEquals(state.publishedAt(), view.publishedAt());
        assertEquals(1, view.summary().clear());
        assertEquals(1, view.summary().moderate());
        assertEquals(1, view.summary().congested());
    }
}
