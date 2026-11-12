package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import com.smarttraffic.domain.history.TrafficHistoryRepository;
import com.smarttraffic.domain.model.SensorReading;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Objects;

@Service
public class TrafficArchiverService {

    private final TrafficHistoryRepository history;
    private final Clock clock;

    public TrafficArchiverService(TrafficHistoryRepository history, Clock clock) {
        this.history = Objects.requireNonNull(history, "history");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public boolean archive(SensorReading reading) {
        return history.append(TrafficHistoryEntry.capture(reading, clock.instant()));
    }
}
