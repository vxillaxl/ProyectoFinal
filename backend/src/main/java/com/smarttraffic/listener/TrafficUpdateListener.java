package com.smarttraffic.listener;


import org.springframework.context.annotation.Profile;
import com.smarttraffic.mapper.DashboardViewMapper;
import com.smarttraffic.service.DashboardBroadcaster;
import com.smarttraffic.config.BrokerNames;
import com.smarttraffic.dto.CityStateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Profile("dashboard")
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
