package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import com.smarttraffic.domain.history.TrafficHistoryRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTrafficHistoryRepository implements TrafficHistoryRepository {

    private final TrafficReadingJpaRepository jpa;
    private final ReadingInsert insert;

    public JpaTrafficHistoryRepository(TrafficReadingJpaRepository jpa, ReadingInsert insert) {
        this.jpa = jpa;
        this.insert = insert;
    }

    @Override
    public boolean append(TrafficHistoryEntry entry) {
        if (jpa.existsByEventId(entry.eventId())) {
            return false;
        }
        try {
            insert.insert(entry);
            return true;
        } catch (DataIntegrityViolationException duplicate) {
            return false;
        }
    }
}
