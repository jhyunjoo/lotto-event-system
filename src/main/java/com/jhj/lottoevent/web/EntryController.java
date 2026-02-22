package com.jhj.lottoevent.web;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.participant.Participant;
import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.ParticipantRepository;
import com.jhj.lottoevent.service.EntryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/event")
public class EntryController {

    private final EntryService entryService;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    public EntryController(EntryService entryService, EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.entryService = entryService;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        Long eventId = eventRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("이벤트 데이터 없음"))
                .getId();

        model.addAttribute("eventId", eventId);
        return "event_join";
    }

    @PostMapping("/join")
    public String join(@RequestParam Long eventId,
                       @RequestParam String phone,
                       Model model) {

        try {
            Entry entry = entryService.join(eventId, phone);

            model.addAttribute("entryNo", entry.getEntryNo());
            model.addAttribute("lottoNumber", entry.getIssuedLottoNumber());
            model.addAttribute("phone", phone);

            return "event_join_result";

        } catch (IllegalStateException e) {
            return "redirect:/auth?phone=" + phone;
        }
    }
}