package com.smarttraffic.domain.congestion;

public final class ClearState implements CongestionState {

    public static final ClearState INSTANCE = new ClearState();

    private ClearState() {
    }

    @Override
    public String label() {
        return "DESPEJADA";
    }

    @Override
    public int severity() {
        return 0;
    }

    @Override
    public String statusPhrase() {
        return "despejada";
    }

    @Override
    public CongestionState transition(double density) {
        if (density >= CongestionThresholds.CONGESTED) {
            return CongestedState.INSTANCE;
        }
        if (density >= CongestionThresholds.MODERATE) {
            return ModerateState.INSTANCE;
        }
        return this;
    }
}
