package com.smarttraffic.domain.congestion;

public final class CongestedState implements CongestionState {

    public static final CongestedState INSTANCE = new CongestedState();

    private CongestedState() {
    }

    @Override
    public String label() {
        return "CONGESTIONADA";
    }

    @Override
    public int severity() {
        return 2;
    }

    @Override
    public String statusPhrase() {
        return "congestionada";
    }

    @Override
    public CongestionState transition(double density) {
        if (density < CongestionThresholds.MODERATE) {
            return ClearState.INSTANCE;
        }
        if (density < CongestionThresholds.CONGESTED) {
            return ModerateState.INSTANCE;
        }
        return this;
    }
}
