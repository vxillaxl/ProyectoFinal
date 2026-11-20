package com.smarttraffic.repository;

import com.smarttraffic.domain.TrafficReadingJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("archiver")
public interface TrafficReadingJpaRepository extends JpaRepository<TrafficReadingJpaEntity, Long> {

    boolean existsByEventId(UUID eventId);

    List<TrafficReadingJpaEntity> findTop50ByOrderByArchivedAtDesc();
}
