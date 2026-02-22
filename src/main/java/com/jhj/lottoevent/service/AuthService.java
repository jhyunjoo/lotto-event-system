package com.jhj.lottoevent.service;

import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.participant.Participant;
import com.jhj.lottoevent.domain.sms.SmsLog;
import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.ParticipantRepository;
import com.jhj.lottoevent.repository.SmsLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthService {

    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;
    private final SmsLogRepository smsLogRepository;

    private final SecureRandom random = new SecureRandom();

    public AuthService(EventRepository eventRepository,
                       ParticipantRepository participantRepository,
                       SmsLogRepository smsLogRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
        this.smsLogRepository = smsLogRepository;
    }

    @Transactional
    public void requestCode(Long eventId, String phone) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        // 참가자 레코드 없으면 생성
        Participant participant = participantRepository.findByEventIdAndPhone(eventId, phone)
                .orElseGet(() -> {
                    Participant p = new Participant();
                    p.setEvent(event);
                    p.setPhone(phone);
                    return participantRepository.save(p);
                });

        String code = generate6DigitCode();

        SmsLog sms = new SmsLog();
        sms.setEvent(event);
        sms.setPhone(phone);
        sms.setType("VERIFY_CODE");
        sms.setMessage("인증번호: " + code);
        sms.setStatus("SUCCESS");
        sms.setEntry(null);
        smsLogRepository.save(sms);
    }

    @Transactional
    public void verifyCode(Long eventId, String phone, String code) {
        SmsLog last = smsLogRepository
                .findTop1ByEventIdAndPhoneAndTypeOrderBySentAtDesc(eventId, phone, "VERIFY_CODE")
                .orElseThrow(() -> new IllegalArgumentException("인증 요청 이력이 없습니다."));

        // message가 "인증번호: 123456" 형태라서 code 추출
        String issued = last.getMessage().replace("인증번호: ", "").trim();
        if (!issued.equals(code)) {
            throw new IllegalArgumentException("인증번호가 일치하지 않습니다.");
        }

        Participant participant = participantRepository.findByEventIdAndPhone(eventId, phone)
                .orElseThrow(() -> new IllegalStateException("참가자 없음"));

        participant.setVerifiedAt(LocalDateTime.now());
        participantRepository.save(participant);
    }

    private String generate6DigitCode() {
        int n = random.nextInt(1_000_000);
        return String.format("%06d", n);
    }
}