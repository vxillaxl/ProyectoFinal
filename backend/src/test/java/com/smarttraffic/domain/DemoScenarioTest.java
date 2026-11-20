package com.smarttraffic.domain;

import com.smarttraffic.domain.density.DensityStrategies;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import com.smarttraffic.domain.sensor.SensorFleet;
import com.smarttraffic.domain.zone.CityTrafficBoard;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoScenarioTest {

    @Test
    void enQuinceMinutosAparecenLosTresEstadosYVariasSubidas() {
        SensorFleet fleet = SensorFleet.regimes(List.of(
                new ZoneId("Zona A"),
                new ZoneId("Zona B"),
                new ZoneId("Zona C"),
                new ZoneId("Zona D")
        ), 41, new Random(2026));
        CityTrafficBoard board = new CityTrafficBoard(DensityStrategies.named("weighted"), 5);
        Set<String> labels = new HashSet<>();
        int raises = 0;
        for (int step = 0; step < 15 * 60 / 2; step++) {
            Instant when = Instant.EPOCH.plusSeconds(step * 2L);
            for (SensorReading reading : fleet.capture(when)) {
                if (board.ingest(reading).levelRaised()) {
                    raises++;
                }
            }
            for (ZoneSnapshot snapshot : board.snapshot()) {
                labels.add(snapshot.state().label());
            }
        }
        assertTrue(labels.contains("DESPEJADA"));
        assertTrue(labels.contains("MODERADA"));
        assertTrue(labels.contains("CONGESTIONADA"));
        assertTrue(raises >= 5, "subidas " + raises);
    }
}
