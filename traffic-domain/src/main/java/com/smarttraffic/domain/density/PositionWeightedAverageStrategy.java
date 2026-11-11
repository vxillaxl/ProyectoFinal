package com.smarttraffic.domain.density;

import com.smarttraffic.domain.model.VehicleCount;

import java.util.List;

/**
 * Promedio ponderado por puesto en la ventana.
 * La muestra más antigua pesa 1, la siguiente 2, y así. La más nueva pesa más.
 * No usa la hora de la lectura.
 */
public final class PositionWeightedAverageStrategy implements DensityStrategy {

    @Override
    public String name() {
        return "weighted";
    }

    @Override
    public double calculate(List<VehicleCount> samplesOldestFirst) {
        List<VehicleCount> samples = SimpleAverageStrategy.requireSamples(samplesOldestFirst);
        if (samples.isEmpty()) {
            return 0.0;
        }
        double weighted = 0.0;
        double weightSum = 0.0;
        for (int index = 0; index < samples.size(); index++) {
            double weight = index + 1.0;
            weighted += samples.get(index).value() * weight;
            weightSum += weight;
        }
        return weighted / weightSum;
    }
}
