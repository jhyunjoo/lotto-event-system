package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.entry.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Optional<Entry> findByParticipantPhone(String phone);
}