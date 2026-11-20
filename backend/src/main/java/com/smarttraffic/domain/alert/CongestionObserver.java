package com.smarttraffic.domain.alert;

/** Recibe el aviso cuando una zona sube de nivel. */
public interface CongestionObserver {

    void onLevelRaised(LevelChange change);
}
