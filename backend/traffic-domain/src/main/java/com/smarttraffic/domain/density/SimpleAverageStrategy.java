package com.smarttraffic.domain.density;

import com.smarttraffic.domain.model.VehicleCount;

import java.util.List;

/** Promedio aritmético de la ventana. Cada lectura pesa lo mismo. */
public final class SimpleAverageStrategy implements DensityStrategy {

    @Override
    public String name() {
        return "simple";
    }

    @Override
    public double calculate(List<VehicleCount> samplesOldestFirst) {
        List<VehicleCount> samples = requireSamples(samplesOldestFirst);
        if (samples.isEmpty()) {
            return 0.0;
        }
        double total = 0.0;
        for (VehicleCount sample : samples) {
            total += sample.value();
        }
        return total / samples.size();
    }

    static List<VehicleCount> requireSamples(List<VehicleCount> samples) {
        if (samples == null) {
            throw new IllegalArgumentException("La ventana de muestras es obligatoria.");
        }
        if (samples.stream().anyMatch(sample -> sample == null)) {
            throw new IllegalArgumentException("La ventana contiene una muestra nula.");
        }
        return samples;
    }
}
