package com.hollywood.doitmoney.user.service;

import java.util.Random;
import java.time.LocalDateTime;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.hollywood.doitmoney.user.entity.EmailVerificationToken;
import com.hollywood.doitmoney.user.repository.EmailVerificationTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final EmailVerificationTokenRepository tokenRepo;
    private final JavaMailSender mailSender;
    @Value("${app.front-base}")
    private String frontBase;

    /** 1) 인증번호 생성·저장·전송 */
    public void sendVerification(String email) {
        // 6자리 숫자 코드 생성
        String code = String.format("%06d", new Random().nextInt(1_000_000));
        var now = LocalDateTime.now();
        tokenRepo.save(EmailVerificationToken.builder()
                .email(email)
                .code(code)
                .expiry(now.plusMinutes(10))
                .build());

        // 이메일 발송
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("[DoitMoney] 이메일 인증번호");
        msg.setText("아래 6자리 인증번호를 입력창에 입력해주세요:\n\n" + code
                + "\n\n(10분 내에 사용해야 합니다)");
        mailSender.send(msg);
    }

    /** 2) 인증번호 검증 */
    // 2단계: 화면에서 코드 확인만 할 때는 삭제하지 않는다.
    public boolean verifyCode(String email, String code) {
        return tokenRepo.findTopByEmailOrderByExpiryDesc(email)
                .filter(t -> t.getCode().equals(code))
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .isPresent(); // ✅ 바로 true/false 반환
    }

    // 3단계: 회원가입 시에만 토큰을 검증하고 삭제한다.
    public void validateCodeForSignup(String email, String code) {
        var evt = tokenRepo.findTopByEmailOrderByExpiryDesc(email)
                .filter(t -> t.getCode().equals(code))
                .filter(t -> t.getExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "코드가 유효하지 않거나 만료되었습니다."));
        tokenRepo.delete(evt);
    }
}
