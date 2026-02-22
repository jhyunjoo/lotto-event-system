package com.jhj.lottoevent.service;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.event.EventSequence;
import com.jhj.lottoevent.domain.event.WinnerSlot;
import com.jhj.lottoevent.domain.participant.Participant;
import com.jhj.lottoevent.domain.result.Result;
import com.jhj.lottoevent.domain.result.ResultView;
import com.jhj.lottoevent.domain.sms.SmsLog;
import com.jhj.lottoevent.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntryService {

    private final EventRepository eventRepository;
    private final EventSequenceRepository sequenceRepository;
    private final ParticipantRepository participantRepository;
    private final EntryRepository entryRepository;
    private final WinnerSlotRepository winnerSlotRepository;
    private final ResultRepository resultRepository;
    private final ResultViewRepository resultViewRepository;
    private final SmsLogRepository smsLogRepository;
    private final LottoNumberGenerator lottoNumberGenerator;

    public EntryService(EventRepository eventRepository,
                        EventSequenceRepository sequenceRepository,
                        ParticipantRepository participantRepository,
                        EntryRepository entryRepository,
                        WinnerSlotRepository winnerSlotRepository,
                        ResultRepository resultRepository,
                        ResultViewRepository resultViewRepository,
                        SmsLogRepository smsLogRepository,
                        LottoNumberGenerator lottoNumberGenerator) {
        this.eventRepository = eventRepository;
        this.sequenceRepository = sequenceRepository;
        this.participantRepository = participantRepository;
        this.entryRepository = entryRepository;
        this.winnerSlotRepository = winnerSlotRepository;
        this.resultRepository = resultRepository;
        this.resultViewRepository = resultViewRepository;
        this.smsLogRepository = smsLogRepository;
        this.lottoNumberGenerator = lottoNumberGenerator;
    }

    @Transactional
    public Entry join(Long eventId, String phone) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        Participant participant = participantRepository
                .findByEventIdAndPhone(eventId, phone)
                .orElseGet(() -> {
                    Participant p = new Participant();
                    p.setEvent(event);
                    p.setPhone(phone);
                    return participantRepository.save(p);
                });

        // 휴대폰 인증 필수
        if (participant.getVerifiedAt() == null) {
            throw new IllegalStateException("휴대폰 인증이 필요합니다.");
        }

        // entry_no 발급 (비관적 락)
        EventSequence sequence = sequenceRepository.findForUpdate(eventId)
                .orElseThrow(() -> new IllegalStateException("시퀀스 없음"));

        int entryNo = sequence.getNextEntryNo();
        sequence.setNextEntryNo(entryNo + 1);

        // ✅ rank 먼저 결정 (fixedFirstPhone 우선)
        byte rank;
        if (phone.equals(event.getFixedFirstPhone())) {
            rank = 1;
        } else {
            rank = winnerSlotRepository
                    .findByEventIdAndEntryNo(eventId, entryNo)
                    .map(WinnerSlot::getRank)
                    .orElse((byte) 0);
        }

        // ✅ rank에 맞춰 suffix 규칙으로 번호 생성
        String issuedNumber = lottoNumberGenerator.generate(event.getWinningNumber(), rank);

        // entry 저장
        Entry entry = new Entry();
        entry.setEvent(event);
        entry.setParticipant(participant);
        entry.setEntryNo(entryNo);
        entry.setIssuedLottoNumber(issuedNumber);

        entry = entryRepository.save(entry);

        // result 저장
        Result result = new Result();
        result.setEvent(event);
        result.setEntry(entry);
        result.setRank(rank);
        resultRepository.save(result);

        // result_view 저장(@MapsId)
        ResultView view = new ResultView();
        view.setEntry(entry);
        resultViewRepository.save(view);

        // sms_log 저장(발급 번호 안내)
        SmsLog sms = new SmsLog();
        sms.setEvent(event);
        sms.setPhone(phone);
        sms.setType("ISSUE_NUMBER");
        sms.setMessage("로또 번호: " + issuedNumber);
        sms.setStatus("SUCCESS");
        sms.setEntry(entry);
        smsLogRepository.save(sms);

        return entry;
    }
}