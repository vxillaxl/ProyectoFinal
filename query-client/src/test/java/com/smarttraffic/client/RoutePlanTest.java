package com.smarttraffic.client;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RoutePlanTest {

    @Test
    void recorreLosTrayectosConfigurados() {
        RoutePlan plan = RoutePlan.parse("Zona A|Zona B;Zona C");
        assertEquals(List.of("Zona A", "Zona B"), plan.next());
        assertEquals(List.of("Zona C"), plan.next());
        assertEquals(List.of("Zona A", "Zona B"), plan.next());
    }

    @Test
    void rechazaUnPlanVacio() {
        assertThrows(IllegalArgumentException.class, () -> RoutePlan.parse("  "));
        assertThrows(IllegalArgumentException.class, () -> RoutePlan.parse("Zona A;;Zona B"));
    }
}
