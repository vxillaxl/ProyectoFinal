package com.smarttraffic.domain.alert;

import com.smarttraffic.domain.congestion.ClearState;
import com.smarttraffic.domain.congestion.CongestedState;
import com.smarttraffic.domain.congestion.ModerateState;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.model.ZoneSnapshot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ZoneStateReplicaTest {

    @Mock
    private CongestionObserver observer;

    private ZoneStateReplica replica;
    private final ZoneId zone = new ZoneId("Zona A");

    @BeforeEach
    void setUp() {
        replica = new ZoneStateReplica(List.of(observer));
    }

    @Test
    void laPrimeraObservacionNoAlerta() {
        replica.replace(List.of(snapshot(CongestedState.INSTANCE, 30)));
        verify(observer, never()).onLevelRaised(any());
    }

    @Test
    void alertaSoloCuandoElNivelSube() {
        replica.replace(List.of(snapshot(ClearState.INSTANCE, 2)));
        replica.replace(List.of(snapshot(ModerateState.INSTANCE, 14)));
        verify(observer).onLevelRaised(any());
    }

    @Test
    void bajarDeNivelNoAlerta() {
        replica.replace(List.of(snapshot(CongestedState.INSTANCE, 30)));
        replica.replace(List.of(snapshot(ClearState.INSTANCE, 1)));
        verify(observer, never()).onLevelRaised(any());
    }

    private ZoneSnapshot snapshot(com.smarttraffic.domain.congestion.CongestionState state, double density) {
        return new ZoneSnapshot(zone, density, state, 4);
    }
}
