package com.smarttraffic.processor;

import com.smarttraffic.domain.model.ZoneSnapshot;
import com.smarttraffic.domain.zone.CityTrafficBoard;
import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.CityStateRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@Component
public class StateBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(StateBroadcaster.class);

    private final CityTrafficBoard board;
    private final RabbitTemplate rabbit;
    private final Clock clock;

    public StateBroadcaster(CityTrafficBoard board, RabbitTemplate rabbit, Clock clock) {
        this.board = board;
        this.rabbit = rabbit;
        this.clock = clock;
    }

    @Scheduled(fixedRateString = "${traffic.broadcast-interval-ms:5000}")
    public void broadcast() {
        List<ZoneSnapshot> snapshot = board.snapshot();
        if (snapshot.isEmpty()) {
            return;
        }
        rabbit.convertAndSend(
                BrokerNames.TRAFFIC_UPDATES,
                "",
                CityStateRecord.from(snapshot, clock.instant())
        );
        log.info("Estado de {} zonas publicado en {}", snapshot.size(), BrokerNames.TRAFFIC_UPDATES);
    }
}
