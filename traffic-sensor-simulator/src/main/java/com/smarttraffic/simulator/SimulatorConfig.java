package com.smarttraffic.simulator;

import com.smarttraffic.domain.model.ZoneCatalog;
import com.smarttraffic.domain.model.ZoneId;
import com.smarttraffic.domain.sensor.SensorFleet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
