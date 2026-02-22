package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.event.WinnerSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WinnerSlotRepository extends JpaRepository<WinnerSlot, Long> {

    long countByEventId(Long eventId);
    long countByEventIdAndRank(Long eventId, Byte rank);

    Optional<WinnerSlot> findByEventIdAndEntryNo(Long eventId, Integer entryNo);
}