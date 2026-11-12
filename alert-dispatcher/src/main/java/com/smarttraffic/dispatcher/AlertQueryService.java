package com.smarttraffic.dispatcher;

import com.smarttraffic.domain.alert.ZoneStateReplica;
import com.smarttraffic.domain.model.ZoneSnapshot;
import com.smarttraffic.domain.query.RouteAnswer;
import com.smarttraffic.domain.query.RouteQuery;

import java.util.List;
import java.util.Objects;

public class AlertQueryService {

    private final ZoneStateReplica replica;

    public AlertQueryService(ZoneStateReplica replica) {
        this.replica = Objects.requireNonNull(replica, "replica");
    }

    public void absorb(List<ZoneSnapshot> snapshots) {
        replica.replace(snapshots);
    }

    public RouteAnswer answer(RouteQuery query) {
        return query.answer(replica.view());
    }
}
