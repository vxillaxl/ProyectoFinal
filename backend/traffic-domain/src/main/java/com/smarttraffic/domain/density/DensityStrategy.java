package com.smarttraffic.domain.density;

import com.smarttraffic.domain.model.VehicleCount;

import java.util.List;

/**
 * Fórmula de densidad. El agregado la recibe por constructor (Strategy)
 * y no conoce cuál implementación está usando.
 */
public interface DensityStrategy {

    String name();

    /**
     * @param samplesOldestFirst lecturas de la ventana, de la más antigua a la más nueva
     */
    double calculate(List<VehicleCount> samplesOldestFirst);
}
