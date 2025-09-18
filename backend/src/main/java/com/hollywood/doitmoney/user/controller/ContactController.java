// doitmoney-backend/src/main/java/com/doitmoney/backend/user/controller/ContactController.java
package com.hollywood.doitmoney.user.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final JavaMailSender mailSender;

    // 문의를 받을 실제 관리자의 이메일 주소
    @Value("${app.contact.email}")
    private String contactAddress; // 예: giveyoufox@gmail.com

    @PostMapping
    public ResponseEntity<String> sendInquiry(@RequestBody ContactRequestDto dto) {
        if (dto.getSubject().isEmpty() || dto.getContent().isEmpty() || dto.getUserEmail().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모든 필드를 채워주세요.");
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(contactAddress);
            msg.setSubject("[고객센터 문의] " + dto.getSubject());
            msg.setText("문의자 이메일: " + dto.getUserEmail() + "\n\n내용:\n" + dto.getContent());
            mailSender.send(msg);
            return ResponseEntity.ok("문의가 성공적으로 전송되었습니다.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송 실패: " + e.getMessage());
        }
    }

    @Getter
    @Setter
    static class ContactRequestDto {
        private String userEmail;
        private String subject;
        private String content;
    }
}