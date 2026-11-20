package com.smarttraffic.listener;


import org.springframework.context.annotation.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.service.TrafficArchiverService;
import com.smarttraffic.config.BrokerNames;
import com.smarttraffic.dto.SensorReadingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
@Profile("archiver")
public class ArchiveListener {

    private static final Logger log = LoggerFactory.getLogger(ArchiveListener.class);

    private final TrafficArchiverService archiver;
    private final ObjectMapper json;
    private final Clock clock;

    public ArchiveListener(TrafficArchiverService archiver, ObjectMapper json, Clock clock) {
        this.archiver = archiver;
        this.json = json;
        this.clock = clock;
    }

    @KafkaListener(topics = BrokerNames.RAW_TRAFFIC, groupId = "traffic-archiver")
    public void onReading(String payload) throws Exception {
        SensorReadingRecord message = json.readValue(payload, SensorReadingRecord.class);
        boolean stored = archiver.archive(message.toDomain(clock.instant()));
        if (!stored) {
            log.info("Lectura {} ya estaba en el histórico", message.eventId());
        }
    }
}
