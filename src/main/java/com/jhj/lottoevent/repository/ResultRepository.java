package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.result.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Integer> {
}
