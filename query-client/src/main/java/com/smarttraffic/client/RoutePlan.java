package com.smarttraffic.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Trayectos codificados como "Zona A|Zona B;Zona C". */
public final class RoutePlan {

    private final List<List<String>> routes;
    private int cursor;

    private RoutePlan(List<List<String>> routes) {
        this.routes = routes;
    }

    public static RoutePlan parse(String encoded) {
        if (encoded == null || encoded.isBlank()) {
            throw new IllegalArgumentException("Debe definir al menos un trayecto.");
        }
        List<List<String>> routes = new ArrayList<>();
        for (String route : encoded.split(";")) {
            List<String> zones = Arrays.stream(route.split("\\|"))
                    .map(String::trim)
                    .filter(zone -> !zone.isEmpty())
                    .toList();
            if (zones.isEmpty()) {
                throw new IllegalArgumentException("Hay un trayecto vacío en la configuración.");
            }
            routes.add(List.copyOf(zones));
        }
        return new RoutePlan(List.copyOf(routes));
    }

    public synchronized List<String> next() {
        List<String> route = routes.get(cursor);
        cursor = (cursor + 1) % routes.size();
        return route;
    }
}
