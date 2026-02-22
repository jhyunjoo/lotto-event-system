package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.entry.Entry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long> {

    Optional<Entry> findByParticipantPhone(String phone);

    @Query("select e from Entry e join fetch e.participant p where e.id in :ids")
    List<Entry> findAllWithParticipantByIdIn(List<Long> ids);
}