package com.smarttraffic.domain.zone;

import com.smarttraffic.domain.congestion.ClearState;
import com.smarttraffic.domain.congestion.CongestedState;
import com.smarttraffic.domain.density.SimpleAverageStrategy;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CityTrafficBoardTest {

    @Test
    void cadaZonaTieneSuAgregado() {
        CityTrafficBoard board = new CityTrafficBoard(new SimpleAverageStrategy(), 5);
        board.ingest(reading("Zona B", 30));
        board.ingest(reading("Zona A", 4));
        board.ingest(reading("Zona B", 28));

        List<ZoneSnapshot> snapshot = board.snapshot();

        assertEquals(List.of("Zona A", "Zona B"), snapshot.stream().map(item -> item.zoneId().value()).toList());
        assertEquals(1, snapshot.get(0).sampleCount());
        assertEquals(4.0, snapshot.get(0).density(), 0.0001);
        assertSame(ClearState.INSTANCE, snapshot.get(0).state());
        assertEquals(2, snapshot.get(1).sampleCount());
        assertEquals(29.0, snapshot.get(1).density(), 0.0001);
        assertSame(CongestedState.INSTANCE, snapshot.get(1).state());
    }

    @Test
    void unaZonaFueraDelCatalogoNoEntraAlTablero() {
        CityTrafficBoard board = new CityTrafficBoard(new SimpleAverageStrategy(), 5);
        board.ingest(reading("Zona A", 4));

        assertThrows(IllegalArgumentException.class, () -> board.ingest(reading("Zona Z", 12)));

        List<ZoneSnapshot> snapshot = board.snapshot();
        assertEquals(List.of("Zona A"), snapshot.stream().map(item -> item.zoneId().value()).toList());
    }

    private static SensorReading reading(String zone, int count) {
        return new SensorReading(UUID.randomUUID(), new ZoneId(zone), VehicleCount.of(count), Instant.EPOCH);
    }
}
