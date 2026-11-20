package com.smarttraffic.domain.density;

import com.smarttraffic.domain.model.VehicleCount;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DensityStrategyTest {

    private final List<VehicleCount> samples = List.of(VehicleCount.of(10), VehicleCount.of(30));

    @Test
    void elPromedioSimplePesaIgualCadaLectura() {
        assertEquals(20.0, new SimpleAverageStrategy().calculate(samples), 0.0001);
        assertEquals(0.0, new SimpleAverageStrategy().calculate(List.of()), 0.0001);
    }

    @Test
    void elPromedioPonderadoFavoreceLaLecturaNueva() {
        double weighted = new PositionWeightedAverageStrategy().calculate(samples);
        assertEquals((10 * 1 + 30 * 2) / 3.0, weighted, 0.0001);
    }

    @Test
    void rechazaUnaVentanaNula() {
        assertThrows(IllegalArgumentException.class, () -> new SimpleAverageStrategy().calculate(null));
    }

    @Test
    void laConfiguracionEligeLaEstrategiaPorNombre() {
        assertEquals("simple", DensityStrategies.named("simple").name());
        assertEquals("weighted", DensityStrategies.named("weighted").name());
        assertThrows(IllegalArgumentException.class, () -> DensityStrategies.named("media"));
    }
}
