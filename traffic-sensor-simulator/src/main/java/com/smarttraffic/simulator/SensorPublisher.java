package com.smarttraffic.simulator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.domain.model.SensorReading;
import com.smarttraffic.domain.sensor.SensorFleet;
import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.SensorReadingRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;

@Component
public class SensorPublisher {

    private static final Logger log = LoggerFactory.getLogger(SensorPublisher.class);

    private final SensorFleet fleet;
    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper json;
    private final Clock clock;

    public SensorPublisher(
            SensorFleet fleet,
            KafkaTemplate<String, String> kafka,
            ObjectMapper json,
            Clock clock
    ) {
        this.fleet = fleet;
        this.kafka = kafka;
        this.json = json;
        this.clock = clock;
    }

    @Scheduled(fixedRateString = "${traffic.publish-interval-ms:2000}")
    public void publish() {
        List<SensorReading> readings = fleet.capture(clock.instant());
        for (SensorReading reading : readings) {
            kafka.send(BrokerNames.RAW_TRAFFIC, reading.zoneId().value(), payload(reading));
        }
        log.info("Publicadas {} lecturas en {}", readings.size(), BrokerNames.RAW_TRAFFIC);
    }

    private String payload(SensorReading reading) {
        try {
            return json.writeValueAsString(SensorReadingRecord.from(reading));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("No se pudo serializar la lectura de " + reading.zoneId(), exception);
        }
    }
}
