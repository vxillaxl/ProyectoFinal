package com.smarttraffic.archiver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
public class HistoryController {

    private final TrafficReadingJpaRepository readings;

    public HistoryController(TrafficReadingJpaRepository readings) {
        this.readings = readings;
    }

    @GetMapping("/api/history")
    public List<HistoryItem> recent() {
        return readings.findTop50ByOrderByArchivedAtDesc().stream()
                .map(HistoryItem::from)
                .toList();
    }

    public record HistoryItem(
            UUID eventId,
            String zoneId,
            int vehicleCount,
            Instant capturedAt,
            Instant archivedAt
    ) {
        static HistoryItem from(TrafficReadingJpaEntity entity) {
            return new HistoryItem(
                    entity.getEventId(),
                    entity.getZoneId(),
                    entity.getVehicleCount(),
                    entity.getCapturedAt(),
                    entity.getArchivedAt()
            );
        }
    }
}
