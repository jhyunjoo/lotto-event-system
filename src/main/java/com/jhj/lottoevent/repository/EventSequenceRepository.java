package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.event.EventSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSequenceRepository extends JpaRepository<EventSequence, Integer> {
}
