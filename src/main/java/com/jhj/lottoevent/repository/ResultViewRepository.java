package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.result.ResultView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResultViewRepository extends JpaRepository<ResultView, Long> {

    @Query("select rv.entryId from ResultView rv where rv.viewCount = 0")
    List<Long> findUncheckedEntryIds();

}