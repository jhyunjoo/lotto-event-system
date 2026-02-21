package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.sms.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsLogRepository extends JpaRepository<SmsLog, Integer> {
}
