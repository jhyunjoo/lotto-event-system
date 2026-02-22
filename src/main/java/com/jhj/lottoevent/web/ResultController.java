package com.jhj.lottoevent.web;

import com.jhj.lottoevent.domain.entry.Entry;
import com.jhj.lottoevent.domain.result.Result;
import com.jhj.lottoevent.domain.result.ResultView;
import com.jhj.lottoevent.repository.EntryRepository;
import com.jhj.lottoevent.repository.ResultRepository;
import com.jhj.lottoevent.repository.ResultViewRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/result")
public class ResultController {

    private final EntryRepository entryRepository;
    private final ResultRepository resultRepository;
    private final ResultViewRepository resultViewRepository;

    public ResultController(EntryRepository entryRepository,
                            ResultRepository resultRepository,
                            ResultViewRepository resultViewRepository) {
        this.entryRepository = entryRepository;
        this.resultRepository = resultRepository;
        this.resultViewRepository = resultViewRepository;
    }

    @GetMapping("/check")
    public String checkPage(@ModelAttribute("phone") String phone, Model model) {
        // flash로 전달된 phone 유지
        model.addAttribute("phone", phone);
        return "result_check";
    }

    @PostMapping("/check")
    public String check(@RequestParam String phone,
                        Model model,
                        RedirectAttributes ra) {

        try {
            Entry entry = entryRepository.findByParticipantPhone(phone)
                    .orElseThrow(() -> new IllegalArgumentException("참가 이력이 없습니다."));

            Result result = resultRepository.findByEntryId(entry.getId())
                    .orElseThrow(() -> new IllegalStateException("결과 데이터가 없습니다."));

            ResultView view = resultViewRepository.findById(entry.getId())
                    .orElseThrow(() -> new IllegalStateException("조회 기록이 없습니다."));

            // ✅ 증가 전이 0이면 '첫 조회'
            boolean firstView = (view.getViewCount() == 0);

            view.setViewCount(view.getViewCount() + 1);
            resultViewRepository.save(view);

            if (firstView) {
                model.addAttribute("message", "당첨 등수: " + rankText(result.getRank()));
            } else {
                model.addAttribute("message",
                        result.getRank() == 0 ? "미당첨" : "당첨되었습니다.");
            }

            return "result_check_result";

        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("flashError", e.getMessage());
            ra.addFlashAttribute("phone", phone);
            return "redirect:/result/check";
        }
    }

    private String rankText(byte rank) {
        if (rank == 0) return "미당첨";
        return rank + "등";
    }
}