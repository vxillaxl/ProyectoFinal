package com.smarttraffic.domain.congestion;

public final class ModerateState implements CongestionState {

    public static final ModerateState INSTANCE = new ModerateState();

    private ModerateState() {
    }

    @Override
    public String label() {
        return "MODERADA";
    }

    @Override
    public int severity() {
        return 1;
    }

    @Override
    public String statusPhrase() {
        return "moderada";
    }

    @Override
    public CongestionState transition(double density) {
        if (density >= CongestionThresholds.CONGESTED) {
            return CongestedState.INSTANCE;
        }
        if (density < CongestionThresholds.MODERATE) {
            return ClearState.INSTANCE;
        }
        return this;
    }
}
