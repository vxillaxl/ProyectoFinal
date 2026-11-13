package com.smarttraffic.archiver.repository;

import com.smarttraffic.archiver.domain.TrafficReadingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TrafficReadingJpaRepository extends JpaRepository<TrafficReadingJpaEntity, Long> {

    boolean existsByEventId(UUID eventId);

    List<TrafficReadingJpaEntity> findTop50ByOrderByArchivedAtDesc();
}
