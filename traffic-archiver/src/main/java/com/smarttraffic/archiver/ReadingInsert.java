package com.smarttraffic.archiver;

import com.smarttraffic.domain.history.TrafficHistoryEntry;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Una transacción por inserción. Si el event_id ya existe, la excepción
 * sale de aquí y el llamador la descarta fuera de esta transacción.
 */
@Component
public class ReadingInsert {

    private final TrafficReadingJpaRepository jpa;

    public ReadingInsert(TrafficReadingJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insert(TrafficHistoryEntry entry) {
        jpa.saveAndFlush(TrafficReadingJpaEntity.from(entry));
    }
}
