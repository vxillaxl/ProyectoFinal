package com.smarttraffic.simulator.config;

import com.smarttraffic.domain.model.ZoneCatalog;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.sensor.SensorFleet;
import com.smarttraffic.messaging.BrokerNames;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

@Configuration
public class SimulatorConfig {

    private static RandomGenerator threadLocal() {
        return new RandomGenerator() {
            @Override
            public int nextInt(int bound) {
                return ThreadLocalRandom.current().nextInt(bound);
            }

            @Override
            public long nextLong() {
                return ThreadLocalRandom.current().nextLong();
            }
        };
    }

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    SensorFleet sensorFleet(
            @Value("${traffic.zones}") List<String> zones,
            @Value("${traffic.max-vehicles}") int maxVehicles
    ) {
        List<ZoneId> ids = zones.stream().map(String::trim).filter(zone -> !zone.isEmpty()).map(ZoneId::new).toList();
        ids.forEach(ZoneCatalog::require);
        return SensorFleet.regimes(ids, maxVehicles, threadLocal());
    }

    /** Declara el topic del enunciado al arrancar (3 particiones, 1 réplica en demo). */
    @Bean
    NewTopic rawTrafficData() {
        return TopicBuilder.name(BrokerNames.RAW_TRAFFIC).partitions(3).replicas(1).build();
    }
}
