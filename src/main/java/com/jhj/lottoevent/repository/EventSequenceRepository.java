package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.event.EventSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventSequenceRepository extends JpaRepository<EventSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select es from EventSequence es where es.eventId = :eventId")
    Optional<EventSequence> findForUpdate(@Param("eventId") Long eventId);
}