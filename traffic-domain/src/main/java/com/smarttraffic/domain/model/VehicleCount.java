package com.smarttraffic.domain.model;

/**
 * Conteo de vehículos de un sensor. El cero es válido (calle vacía);
 * un negativo no representa una lectura real y se rechaza al nacer.
 */
public record VehicleCount(int value) {

    public VehicleCount {
        if (value < 0) {
            throw new IllegalArgumentException("El conteo de vehículos no puede ser negativo: " + value);
        }
    }

    public static VehicleCount of(int value) {
        return new VehicleCount(value);
    }
}
