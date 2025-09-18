package com.hollywood.doitmoney.user.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.hollywood.doitmoney.user.entity.PasswordResetToken;
import com.hollywood.doitmoney.user.repository.PasswordResetTokenRepository;
import com.hollywood.doitmoney.user.repository.UserRepository;

@RestController
@RequestMapping("/api/recover")
@RequiredArgsConstructor
public class RecoverController {

    private final UserRepository repo;
    private final JavaMailSender mail;
    private final PasswordEncoder enc;
    private final PasswordResetTokenRepository tokenRepo;

    /* ── ① ID(이메일) 찾기 ── */
    @GetMapping("/find-id")
    public Map<String, String> findId(@RequestParam String phone) {
        return repo.findByPhone(phone)
                .<Map<String, String>>map(u -> Map.of("email", mask(u.getEmail())))
                .orElse(Map.of("email", ""));
    }

    private String mask(String email) {
        // junzzang@gmail.com → j***z**@gm***.c**
        int at = email.indexOf('@');
        String id = email.substring(0, at);
        String dom = email.substring(at + 1);
        id = id.replaceAll("(?<=.).(?=.*.)", "*");
        dom = dom.replaceAll("(?<=.).(?=.*.)", "*");
        return id + "@" + dom;
    }

    /* ── ② 재설정 메일 발송 ── */
    @PostMapping("/reset-mail")
    public ResponseEntity<Void> sendMail(@RequestBody Map<String, String> body,
            @Value("${app.front-base}") String front) {
        var user = repo.findByEmail(body.get("email"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 아님"));

        String token = UUID.randomUUID().toString();
        tokenRepo.save(new PasswordResetToken(null, token, user,
                LocalDateTime.now().plusMinutes(30)));

        String link = front + "/reset-password?token=" + token;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("[Planary] 비밀번호 재설정");
        msg.setText("""
                30분 이내 아래 링크로 이동하여 새 비밀번호를 설정해주세요.

                %s
                """.formatted(link));
        mail.send(msg);
        return ResponseEntity.ok().build();
    }

    /* ── ③ 새 비밀번호 저장 ── */
    @PostMapping("/reset-password")
    public void reset(@RequestBody Map<String, String> b) {
        String token = b.get("token");
        String pw = b.get("password");

        var prt = tokenRepo.findByToken(token)
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰 오류/만료"));
        var u = prt.getUser();
        u.setPassword(enc.encode(pw));
        repo.save(u);
        tokenRepo.delete(prt);
    }

    /* ===== DTO (프론트 사용 편의용) ===== */
    @Getter
    @Setter
    public static class ResetMailReq {
        private String email;
    }
}