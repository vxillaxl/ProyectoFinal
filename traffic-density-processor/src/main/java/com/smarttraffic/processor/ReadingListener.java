package com.smarttraffic.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.domain.zone.CityTrafficBoard;
import com.smarttraffic.domain.zone.CongestionTransition;
import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.SensorReadingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Locale;

@Component
public class ReadingListener {

    private static final Logger log = LoggerFactory.getLogger(ReadingListener.class);

    private final CityTrafficBoard board;
    private final ObjectMapper json;
    private final Clock clock;

    public ReadingListener(CityTrafficBoard board, ObjectMapper json, Clock clock) {
        this.board = board;
        this.json = json;
        this.clock = clock;
    }

    @KafkaListener(topics = BrokerNames.RAW_TRAFFIC, groupId = "traffic-density-processor")
    public void onReading(String payload) throws Exception {
        SensorReadingRecord message = json.readValue(payload, SensorReadingRecord.class);
        CongestionTransition transition = board.ingest(message.toDomain(clock.instant()));
        if (transition.levelRaised()) {
            log.info(
                    "{} pasó de {} a {} (densidad {})",
                    transition.zoneId(),
                    transition.previous().label(),
                    transition.current().label(),
                    String.format(Locale.US, "%.1f", transition.density())
            );
        }
    }
}
