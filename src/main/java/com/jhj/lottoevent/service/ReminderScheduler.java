package com.jhj.lottoevent.service;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.sms.SmsLog;
import com.jhj.lottoevent.repository.EntryRepository;
import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.ResultViewRepository;
import com.jhj.lottoevent.repository.SmsLogRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private final EventRepository eventRepository;
    private final ResultViewRepository resultViewRepository;
    private final EntryRepository entryRepository;
    private final SmsLogRepository smsLogRepository;

    public ReminderScheduler(EventRepository eventRepository,
                             ResultViewRepository resultViewRepository,
                             EntryRepository entryRepository,
                             SmsLogRepository smsLogRepository) {
        this.eventRepository = eventRepository;
        this.resultViewRepository = resultViewRepository;
        this.entryRepository = entryRepository;
        this.smsLogRepository = smsLogRepository;
    }

    // 매일 09:00 실행
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendReminderForUnchecked() {
        System.out.println("[ReminderScheduler] triggered at " + LocalDateTime.now());

        Event event = eventRepository.findAll().stream().findFirst().orElse(null);
        if (event == null) return;

        LocalDateTime now = LocalDateTime.now();

        // 발표 시작 + 10일 이후부터 발송
        if (now.isBefore(event.getAnnounceStartAt().plusDays(10))) return;

        // 발표 기간이 아니면 발송 안 함(요구조건에 맞춤)
        if (now.isAfter(event.getAnnounceEndAt())) return;

        List<Long> uncheckedEntryIds = resultViewRepository.findUncheckedEntryIds();
        if (uncheckedEntryIds.isEmpty()) return;

        List<Entry> entries = entryRepository.findAllWithParticipantByIdIn(uncheckedEntryIds);

        for (Entry e : entries) {
            String phone = e.getParticipant().getPhone();

            if (smsLogRepository.existsByEventIdAndEntry_IdAndType(event.getId(), e.getId(), "REMIND_UNCHECKED")) {
                continue;
            }

            SmsLog sms = new SmsLog();
            sms.setEvent(event);
            sms.setPhone(phone);
            sms.setType("REMIND_UNCHECKED");
            sms.setMessage("로또 이벤트 결과를 확인해주세요. (미확인 상태)");
            sms.setStatus("SUCCESS");
            sms.setEntry(e);

            smsLogRepository.save(sms);

            System.out.println("[ReminderScheduler] send REMIND_UNCHECKED to " + phone);
        }
    }
}