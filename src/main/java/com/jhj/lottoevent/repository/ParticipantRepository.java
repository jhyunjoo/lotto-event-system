package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.participant.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {

    Optional<Participant> findByEventIdAndPhone(Long eventId, String phone);
}
