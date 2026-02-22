package com.jhj.lottoevent;

import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.event.EventSequence;
import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.EventSequenceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final EventSequenceRepository eventSequenceRepository;

    public DataInitializer(EventRepository eventRepository, EventSequenceRepository eventSequenceRepository) {
        this.eventRepository = eventRepository;
        this.eventSequenceRepository = eventSequenceRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {

        // 1) 이벤트가 없으면 생성
        Event event = eventRepository.findAll().stream().findFirst().orElse(null);

        if (event == null) {
            Event e = new Event();
            e.setName("모바일팩토리 로또 이벤트");
            e.setEventStartAt(LocalDateTime.of(2025, 2, 1, 0, 0));
            e.setEventEndAt(LocalDateTime.of(2025, 3, 31, 23, 59, 59));
            e.setAnnounceStartAt(LocalDateTime.of(2025, 4, 1, 0, 0));
            e.setAnnounceEndAt(LocalDateTime.of(2025, 4, 15, 23, 59, 59));
            e.setWinningNumber("123456");
            e.setFixedFirstPhone("010-0000-0000");
            e.setMaxParticipants(10000);

            event = eventRepository.save(e);
        }

        // 2) event_sequence가 없으면 생성 (@MapsId)
        if (eventSequenceRepository.findById(event.getId()).isEmpty()) {
            EventSequence seq = new EventSequence();
            seq.setEvent(event);
            seq.setNextEntryNo(1);
            eventSequenceRepository.save(seq);
        }
    }
}