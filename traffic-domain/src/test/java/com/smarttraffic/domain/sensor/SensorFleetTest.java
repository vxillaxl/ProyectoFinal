package com.smarttraffic.domain.sensor;

import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SensorFleetTest {

    @Test
    void unaCapturaGeneraUnaLecturaValidaPorZona() {
        UUID eventId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        SensorFleet fleet = new SensorFleet(
                List.of(new ZoneId("Zona A"), new ZoneId("Zona B")),
                () -> 25,
                () -> eventId
        );
        var readings = fleet.capture(Instant.parse("2026-11-09T15:00:00Z"));
        assertEquals(2, readings.size());
        assertEquals(25, readings.get(0).vehicleCount().value());
        assertEquals(eventId, readings.get(1).eventId());
    }

    @Test
    void exigeAlMenosUnaZona() {
        assertThrows(IllegalArgumentException.class, () -> new SensorFleet(List.of(), () -> 0, UUID::randomUUID));
    }

    @Test
    void unaZonaSinCatalogoFallaAlArrancar() {
        assertThrows(IllegalArgumentException.class, () ->
                com.smarttraffic.domain.model.ZoneCatalog.require(new ZoneId("Zona E")));
    }
}
