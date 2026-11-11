package com.smarttraffic.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.sensor.SensorFleet;
import com.smarttraffic.messaging.BrokerNames;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafka;
    @Mock
    private Clock clock;

    @Test
    void publicaUnaLecturaPorZonaUsandoLaZonaComoClave() {
        when(clock.instant()).thenReturn(Instant.EPOCH);
        UUID eventId = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        SensorFleet fleet = new SensorFleet(
                List.of(new ZoneId("Zona A"), new ZoneId("Zona B")),
                () -> 8,
                () -> eventId
        );
        ObjectMapper json = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        SensorPublisher publisher = new SensorPublisher(fleet, kafka, json, clock);
        publisher.publish();
        verify(kafka).send(eq(BrokerNames.RAW_TRAFFIC), eq("Zona A"), anyString());
        verify(kafka).send(eq(BrokerNames.RAW_TRAFFIC), eq("Zona B"), anyString());
    }
}
