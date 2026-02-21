package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
}
