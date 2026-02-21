package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.entry.Entry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntryRepository extends JpaRepository<Entry, Long> {}