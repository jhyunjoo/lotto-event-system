package com.jhj.lottoevent.service;

import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.participant.Participant;
import com.jhj.lottoevent.domain.sms.SmsLog;
import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.ParticipantRepository;
import com.jhj.lottoevent.repository.SmsLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final SmsLogRepository smsLogRepository;

    public AuthService(EventRepository eventRepository,
                       ParticipantRepository participantRepository,
                       SmsLogRepository smsLogRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.smsLogRepository = smsLogRepository;
    }

    @Transactional
    public LocalDateTime requestCode(Long eventId, String phone) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        Participant p = participantRepository.findByEventIdAndPhone(eventId, phone)
                .orElseGet(() -> {
                    Participant np = new Participant();
                    np.setEvent(event);
                    np.setPhone(phone);
                    return participantRepository.save(np);
                });

        String code = generate6Digit();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(3);

        p.setVerifyCode(code);
        p.setVerifyExpiresAt(expiresAt);

        // 실제 SMS 대신 로그
        SmsLog sms = new SmsLog();
        sms.setEvent(event);
        sms.setPhone(phone);
        sms.setType("VERIFY_CODE");
        sms.setMessage("인증번호: " + code);
        sms.setStatus("SUCCESS");
        smsLogRepository.save(sms);

        return expiresAt;
    }

    @Transactional
    public void verifyCode(Long eventId, String phone, String code) {
        Participant p = participantRepository.findByEventIdAndPhone(eventId, phone)
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 이력이 없습니다."));

        if (p.getVerifyCode() == null || p.getVerifyExpiresAt() == null) {
            throw new IllegalArgumentException("인증번호를 먼저 요청해주세요.");
        }

        if (LocalDateTime.now().isAfter(p.getVerifyExpiresAt())) {
            throw new IllegalArgumentException("인증번호가 만료되었습니다. 다시 요청해주세요.");
        }

        if (!p.getVerifyCode().equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        p.setVerifiedAt(LocalDateTime.now());
        p.setVerifyCode(null);
        p.setVerifyExpiresAt(null);

    }

    @Transactional(readOnly = true)
    public LocalDateTime getExpiresAt(Long eventId, String phone) {
        return participantRepository.findByEventIdAndPhone(eventId, phone)
                .map(Participant::getVerifyExpiresAt)
                .orElse(null);
    }

    private String generate6Digit() {
        int n = ThreadLocalRandom.current().nextInt(0, 1_000_000);
        return String.format("%06d", n);
    }
}