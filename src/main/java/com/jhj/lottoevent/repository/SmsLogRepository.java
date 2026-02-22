package com.jhj.lottoevent.repository;

import com.jhj.lottoevent.domain.sms.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {

    boolean existsByEventIdAndEntry_IdAndType(Long eventId, Long entryId, String type);
    Optional<SmsLog> findTop1ByEventIdAndPhoneAndTypeOrderBySentAtDesc(Long eventId, String phone, String type);
}