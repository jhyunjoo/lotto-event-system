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

    public EntryService(EventRepository eventRepository,
                        EventSequenceRepository sequenceRepository,
                        ParticipantRepository participantRepository,
                        EntryRepository entryRepository,
                        WinnerSlotRepository winnerSlotRepository,
                        ResultRepository resultRepository,
                        ResultViewRepository resultViewRepository,
                        SmsLogRepository smsLogRepository) {
        this.eventRepository = eventRepository;
        this.sequenceRepository = sequenceRepository;
        this.participantRepository = participantRepository;
        this.entryRepository = entryRepository;
        this.winnerSlotRepository = winnerSlotRepository;
        this.resultRepository = resultRepository;
        this.resultViewRepository = resultViewRepository;
        this.smsLogRepository = smsLogRepository;
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

        EventSequence sequence = sequenceRepository.findForUpdate(eventId)
                .orElseThrow(() -> new IllegalStateException("시퀀스 없음"));

        int entryNo = sequence.getNextEntryNo();
        sequence.setNextEntryNo(entryNo + 1);

        Entry entry = new Entry();
        entry.setEvent(event);
        entry.setParticipant(participant);
        entry.setEntryNo(entryNo);
        entry.setIssuedLottoNumber(generateNumber());

        entry = entryRepository.save(entry);

        byte rank;

        if (phone.equals(event.getFixedFirstPhone())) {
            // fixedFirstPhone은 1등 고정
            rank = 1;
        } else {
            rank = winnerSlotRepository
                    .findByEventIdAndEntryNo(eventId, entryNo)
                    .map(WinnerSlot::getRank)
                    .orElse((byte) 0);
        }

        Result result = new Result();
        result.setEvent(event);
        result.setEntry(entry);
        result.setRank(rank);
        resultRepository.save(result);

        ResultView view = new ResultView();
        view.setEntry(entry);          // @MapsId라 entry만 set해도 entryId 채워짐
        resultViewRepository.save(view);

        SmsLog sms = new SmsLog();
        sms.setEvent(event);
        sms.setPhone(phone);
        sms.setType("ISSUE_NUMBER");
        sms.setMessage("로또 번호: " + entry.getIssuedLottoNumber());
        sms.setStatus("SUCCESS");
        sms.setEntry(entry);
        smsLogRepository.save(sms);

        return entry;
    }

    private String generateNumber() {
        return String.format("%06d", (int) (Math.random() * 1_000_000));
    }
}