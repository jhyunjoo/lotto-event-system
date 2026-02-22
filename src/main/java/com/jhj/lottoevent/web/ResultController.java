package com.jhj.lottoevent.web;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.result.Result;
import com.jhj.lottoevent.domain.result.ResultView;
import com.jhj.lottoevent.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/result")
public class ResultController {

    private final EntryRepository entryRepository;
    private final ResultRepository resultRepository;
    private final ResultViewRepository resultViewRepository;
    private final ParticipantRepository participantRepository;

    public ResultController(EntryRepository entryRepository,
                            ResultRepository resultRepository,
                            ResultViewRepository resultViewRepository,
                            ParticipantRepository participantRepository) {
        this.entryRepository = entryRepository;
        this.resultRepository = resultRepository;
        this.resultViewRepository = resultViewRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/check")
    public String checkPage() {
        return "result_check";
    }

    @PostMapping("/check")
    public String check(@RequestParam String phone, Model model) {

        Entry entry = entryRepository.findByParticipantPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("참가 이력이 없습니다."));

        Result result = resultRepository.findByEntryId(entry.getId())
                .orElseThrow(() -> new IllegalStateException("결과 없음"));

        ResultView view = resultViewRepository.findById(entry.getId())
                .orElseThrow(() -> new IllegalStateException("조회 기록 없음"));

        view.setViewCount(view.getViewCount() + 1);

        resultViewRepository.save(view);

        if (view.getViewCount() == 1) {
            model.addAttribute("message", "당첨 등수: " + rankText(result.getRank()));
        } else {
            model.addAttribute("message",
                    result.getRank() == 0 ? "미당첨" : "당첨되었습니다.");
        }

        return "result_check_result";
    }

    private String rankText(byte rank) {
        if (rank == 0) return "미당첨";
        return rank + "등";
    }
}