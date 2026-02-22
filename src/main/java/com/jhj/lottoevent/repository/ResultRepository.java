package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.result.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {

    Optional<Result> findByEntryId(Long entryId);
}