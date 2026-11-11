package com.smarttraffic.domain.model;

import java.util.Map;

/**
 * Coordenadas fijas de las cuatro zonas de la demo, en Bogotá.
 */
public final class ZoneCatalog {

    private static final Map<String, Coordinates> ZONES = Map.of(
            "Zona A", new Coordinates(4.5981, -74.0760),
            "Zona B", new Coordinates(4.6486, -74.0628),
            "Zona C", new Coordinates(4.6290, -74.1515),
            "Zona D", new Coordinates(4.6700, -74.1410)
    );

    private ZoneCatalog() {
    }

    public static Coordinates require(ZoneId zone) {
        Coordinates coordinates = ZONES.get(zone.value());
        if (coordinates == null) {
            throw new IllegalArgumentException("No hay coordenadas para " + zone.value() + ".");
        }
        return coordinates;
    }
}
