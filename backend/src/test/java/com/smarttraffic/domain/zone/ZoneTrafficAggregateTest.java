package com.smarttraffic.domain.zone;

import com.smarttraffic.domain.congestion.ClearState;
import com.smarttraffic.domain.congestion.CongestedState;
import com.smarttraffic.domain.congestion.ModerateState;
import com.smarttraffic.domain.density.DensityStrategy;
import com.smarttraffic.domain.density.SimpleAverageStrategy;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.model.VehicleCount;
import com.smarttraffic.domain.model.ZoneId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZoneTrafficAggregateTest {

    private final ZoneId zone = new ZoneId("Zona C");

    @Mock
    private DensityStrategy strategy;

    @Test
    void veinticincoVehiculosEnUnaMuestraEsCongestion() {
        ZoneTrafficAggregate aggregate = ZoneTrafficAggregate.open(zone, new SimpleAverageStrategy(), 1);
        CongestionTransition transition = aggregate.record(reading(zone, 25));
        assertSame(CongestedState.INSTANCE, transition.current());
        assertTrue(transition.levelRaised());
        assertEquals(25.0, transition.density(), 0.0001);
    }

    @Test
    void laVentanaSueltaLaLecturaMasVieja() {
        ZoneTrafficAggregate aggregate = ZoneTrafficAggregate.open(zone, new SimpleAverageStrategy(), 2);
        aggregate.record(reading(zone, 10));
        aggregate.record(reading(zone, 20));
        CongestionTransition last = aggregate.record(reading(zone, 30));
        assertEquals(25.0, last.density(), 0.0001);
        assertSame(CongestedState.INSTANCE, last.current());
    }

    @Test
    void laFormulaLaCalculaLaEstrategiaInyectada() {
        when(strategy.calculate(anyList())).thenReturn(12.0);
        ZoneTrafficAggregate aggregate = ZoneTrafficAggregate.open(zone, strategy, 3);
        CongestionTransition transition = aggregate.record(reading(zone, 1));
        assertEquals(12.0, transition.density(), 0.0001);
        assertSame(ModerateState.INSTANCE, transition.current());
    }

    @Test
    void rechazaUnaLecturaDeOtraZona() {
        ZoneTrafficAggregate aggregate = ZoneTrafficAggregate.open(zone, new SimpleAverageStrategy(), 3);
        assertThrows(IllegalArgumentException.class, () -> aggregate.record(reading(new ZoneId("Zona A"), 4)));
        assertSame(ClearState.INSTANCE, aggregate.snapshot().state());
    }

    private static SensorReading reading(ZoneId zoneId, int count) {
        return new SensorReading(UUID.randomUUID(), zoneId, VehicleCount.of(count), Instant.EPOCH);
    }
}
