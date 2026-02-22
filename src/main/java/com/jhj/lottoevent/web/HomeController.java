package com.jhj.lottoevent.web;

import com.jhj.lottoevent.domain.event.Event;
import com.jhj.lottoevent.repository.EventRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;

@Controller
public class HomeController {

    private final EventRepository eventRepository;

    public HomeController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @GetMapping("/")
    public String home() {
        Event event = eventRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("이벤트 데이터 없음"));

        // LocalDateTime now = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.of(2025, 2, 10, 0, 0);  // 이벤트 참여 테스트용
        //LocalDateTime now = LocalDateTime.of(2025, 4, 2, 0, 0);     // 이벤트 결과 테스트용

        // 이벤트 기간
        if (!now.isBefore(event.getEventStartAt()) && !now.isAfter(event.getEventEndAt())) {
            return "redirect:/event/join";
        }

        // 발표 기간 (다음 단계에서 구현할 페이지)
        if (!now.isBefore(event.getAnnounceStartAt()) && !now.isAfter(event.getAnnounceEndAt())) {
            return "redirect:/result/check";
        }

        return "redirect:/closed";
    }

    @GetMapping("/closed")
    public String closed() {
        return "closed";
    }
}