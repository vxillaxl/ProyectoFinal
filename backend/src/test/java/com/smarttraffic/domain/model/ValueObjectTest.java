package com.smarttraffic.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValueObjectTest {

    @Test
    void rechazaZonaVaciaYConteoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new ZoneId("  "));
        assertThrows(IllegalArgumentException.class, () -> VehicleCount.of(-1));
    }

    @Test
    void recortaLaZonaYAceptaCalleVacia() {
        assertEquals("Zona A", new ZoneId("  Zona A ").value());
        assertEquals(0, VehicleCount.of(0).value());
    }

    @Test
    void unaLecturaIncompletaFalla() {
        assertThrows(NullPointerException.class, () -> new SensorReading(
                null,
                new ZoneId("Zona A"),
                VehicleCount.of(1),
                Instant.EPOCH
        ));
    }

    @Test
    void laClaveDelClienteSigueElContrato() {
        ClientId client = new ClientId("client_456");
        assertEquals("answer.client_456", client.answerRoutingKey());
        assertThrows(IllegalArgumentException.class, () -> new ClientId("client 456"));
    }

    @Test
    void lasCoordenadasValidasSeConservanYLasInvalidasFallan() {
        Coordinates bogota = new Coordinates(4.5981, -74.0760);
        assertEquals(4.5981, bogota.latitude());
        assertEquals(-74.0760, bogota.longitude());
        assertEquals(-90, new Coordinates(-90, -180).latitude());
        assertEquals(180, new Coordinates(90, 180).longitude());
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(90.1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(0, 180.1));
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(Double.NaN, 0));
        assertThrows(IllegalArgumentException.class, () -> new Coordinates(0, Double.POSITIVE_INFINITY));
    }

    @Test
    void elCatalogoEntregaLasCuatroZonas() {
        assertEquals(4.5981, ZoneCatalog.require(new ZoneId("Zona A")).latitude());
        assertEquals(-74.0628, ZoneCatalog.require(new ZoneId("Zona B")).longitude());
        assertEquals(4.6290, ZoneCatalog.require(new ZoneId("Zona C")).latitude());
        assertEquals(-74.1410, ZoneCatalog.require(new ZoneId("Zona D")).longitude());
    }

    @Test
    void unaDensidadNegativaFalla() {
        assertThrows(IllegalArgumentException.class, () -> new ZoneSnapshot(
                new ZoneId("Zona A"),
                -1,
                com.smarttraffic.domain.congestion.ClearState.INSTANCE,
                1
        ));
    }

    @Test
    void elHistoricoConservaLaLecturaOriginal() {
        SensorReading reading = new SensorReading(
                UUID.randomUUID(),
                new ZoneId("Zona C"),
                VehicleCount.of(25),
                Instant.parse("2026-11-09T12:00:00Z")
        );
        var entry = com.smarttraffic.domain.history.TrafficHistoryEntry.capture(
                reading,
                Instant.parse("2026-11-09T12:00:01Z")
        );
        assertEquals(reading.eventId(), entry.eventId());
        assertEquals(25, entry.vehicleCount().value());
    }
}
