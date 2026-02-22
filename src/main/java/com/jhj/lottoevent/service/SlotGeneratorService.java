package com.jhj.lottoevent.service;

import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.domain.event.WinnerSlot;
import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.WinnerSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class SlotGeneratorService {

    private final EventRepository eventRepository;
    private final WinnerSlotRepository winnerSlotRepository;

    public SlotGeneratorService(EventRepository eventRepository, WinnerSlotRepository winnerSlotRepository) {
        this.eventRepository = eventRepository;
        this.winnerSlotRepository = winnerSlotRepository;
    }

    @Transactional
    public void generateSlots(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("이벤트 없음"));

        // 이미 생성되어 있으면 중복 생성 방지
        if (winnerSlotRepository.countByEventId(eventId) > 0) return;

        Random random = new Random();

        Set<Integer> used = new HashSet<>();

        // 2등: 5명 (2000~7000)
        pick(event, used, random, 2000, 7000, 5, (byte)2);

        // 3등: 44명 (1000~8000)
        pick(event, used, random, 1000, 8000, 44, (byte)3);

        // 4등: 950명 (1~10000)
        pick(event, used, random, 1, event.getMaxParticipants(), 950, (byte)4);
    }

    private void pick(Event event, Set<Integer> used, Random random,
                      int min, int max, int count, byte rank) {

        int range = max - min + 1;
        if (range < count) throw new IllegalArgumentException("범위가 당첨자 수보다 작음");

        int created = 0;
        while (created < count) {
            int entryNo = min + random.nextInt(range);

            if (used.add(entryNo)) {
                WinnerSlot slot = new WinnerSlot();
                slot.setEvent(event);
                slot.setEntryNo(entryNo);
                slot.setRank(rank);
                winnerSlotRepository.save(slot);
                created++;
            }
        }
    }
}