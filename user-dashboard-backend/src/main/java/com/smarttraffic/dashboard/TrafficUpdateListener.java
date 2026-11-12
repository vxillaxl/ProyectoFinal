package com.smarttraffic.dashboard;

import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.CityStateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TrafficUpdateListener {

    private static final Logger log = LoggerFactory.getLogger(TrafficUpdateListener.class);

    private final DashboardBroadcaster broadcaster;

    public TrafficUpdateListener(DashboardBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @RabbitListener(queues = BrokerNames.DASHBOARD_QUEUE)
    public void onState(CityStateRecord state) {
        broadcaster.publish(DashboardViewMapper.toView(state));
        log.info("Dashboard notificado con {} zonas", state.zones().size());
    }
}
