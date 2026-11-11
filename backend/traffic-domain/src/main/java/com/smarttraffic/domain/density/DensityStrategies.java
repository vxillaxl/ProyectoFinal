package com.smarttraffic.domain.density;

/** Punto de composición: elige la estrategia por nombre de configuración. */
public final class DensityStrategies {

    private DensityStrategies() {
    }

    public static DensityStrategy named(String name) {
        if ("simple".equals(name)) {
            return new SimpleAverageStrategy();
        }
        if ("weighted".equals(name)) {
            return new PositionWeightedAverageStrategy();
        }
        throw new IllegalArgumentException("Estrategia desconocida: " + name + ". Use simple o weighted.");
    }
}
