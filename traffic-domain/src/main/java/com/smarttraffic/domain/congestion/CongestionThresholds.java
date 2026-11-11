package com.smarttraffic.domain.congestion;

/**
 * Umbrales del negocio: despejada por debajo de 10, moderada hasta 24
 * y congestionada desde 25.
 */
final class CongestionThresholds {

    static final double MODERATE = 10.0;
    static final double CONGESTED = 25.0;

    private CongestionThresholds() {
    }
}
