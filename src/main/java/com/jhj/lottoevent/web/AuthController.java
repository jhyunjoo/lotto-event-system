package com.jhj.lottoevent.web;

import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.repository.SmsLogRepository;
import com.jhj.lottoevent.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final EventRepository eventRepository;
    private final AuthService authService;
    private final SmsLogRepository smsLogRepository;

    public AuthController(EventRepository eventRepository,
                          AuthService authService,
                          SmsLogRepository smsLogRepository) {
        this.eventRepository = eventRepository;
        this.authService = authService;
        this.smsLogRepository = smsLogRepository;
    }

    @GetMapping
    public String authPage(@RequestParam(required = false) String phone,
                           Model model) {

        Long eventId = eventRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("이벤트 데이터 없음"))
                .getId();

        model.addAttribute("eventId", eventId);
        model.addAttribute("phone", phone);

        return "auth_request";
    }

    @PostMapping("/request")
    public String request(@RequestParam Long eventId,
                          @RequestParam String phone,
                          Model model) {
        authService.requestCode(eventId, phone);

        // 개발 편의: 최신 인증코드 보여주기(제출용이면 숨겨도 됨)
        String lastMsg = smsLogRepository.findTop1ByEventIdAndPhoneAndTypeOrderBySentAtDesc(eventId, phone, "VERIFY_CODE")
                .map(s -> s.getMessage())
                .orElse("");

        model.addAttribute("eventId", eventId);
        model.addAttribute("phone", phone);
        model.addAttribute("debugCodeMsg", lastMsg);

        return "auth_verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam Long eventId,
                         @RequestParam String phone,
                         @RequestParam String code,
                         Model model) {
        authService.verifyCode(eventId, phone, code);

        model.addAttribute("eventId", eventId);
        model.addAttribute("phone", phone);
        model.addAttribute("verified", true);

        // 인증 완료 후 참가 페이지로 이동 링크
        return "auth_verified";
    }
}