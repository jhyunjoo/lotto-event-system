package com.jhj.lottoevent.web;

import com.jhj.lottoevent.repository.EventRepository;
import com.jhj.lottoevent.service.AuthService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final EventRepository eventRepository;
    private final AuthService authService;

    public AuthController(EventRepository eventRepository, AuthService authService) {
        this.eventRepository = eventRepository;
        this.authService = authService;
    }

    @GetMapping
    public String authPage(@RequestParam(required = false) String phone, Model model) {
        Long eventId = eventRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("이벤트 데이터 없음"))
                .getId();

        model.addAttribute("eventId", eventId);
        model.addAttribute("phone", phone);
        return "auth_request";
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam String phone, Model model) {
        Long eventId = eventRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("이벤트 데이터 없음"))
                .getId();

        LocalDateTime expiresAt = authService.getExpiresAt(eventId, phone);

        model.addAttribute("eventId", eventId);
        model.addAttribute("phone", phone);
        model.addAttribute("expiresAtEpochMs", expiresAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return "auth_verify";
    }

    @PostMapping("/request")
    public String request(@RequestParam Long eventId,
                          @RequestParam String phone,
                          RedirectAttributes ra) {

        authService.requestCode(eventId, phone);
        ra.addFlashAttribute("flashSuccess", "인증번호를 발송했습니다. 3분 내에 입력해주세요.");
        return "redirect:/auth/verify?phone=" + phone;
    }

    @PostMapping("/verify")
    public String verify(@RequestParam Long eventId,
                         @RequestParam String phone,
                         @RequestParam String code,
                         RedirectAttributes ra) {
        try {
            authService.verifyCode(eventId, phone, code);
            ra.addFlashAttribute("flashSuccess", "인증이 완료되었습니다. 참가를 진행해주세요.");
            return "redirect:/event/join";
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("flashError", e.getMessage());
            return "redirect:/auth/verify?phone=" + phone;
        }
    }
}