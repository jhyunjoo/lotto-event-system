package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.event.WinnerSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerSlotRepository extends JpaRepository<WinnerSlot, Integer> {
}
