package com.hollywood.doitmoney.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// ↓ 이 줄이 빠져있어서 Authentication을 못 찾고 있었습니다.
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import com.hollywood.doitmoney.user.dto.VerificationResponseDto;
import com.hollywood.doitmoney.user.dto.CompleteSignupReq;
import com.hollywood.doitmoney.user.dto.UserDto;
import com.hollywood.doitmoney.user.dto.VerificationCodeDto;
import com.hollywood.doitmoney.user.service.EmailVerificationService;
import com.hollywood.doitmoney.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final EmailVerificationService verifSvc;
    private final UserService userService;
    private final AuthenticationManager authManager;

    // 이메일 인증 없이 바로 가입하는 디버그용 API를 임시
    @PostMapping("/dev-register")
    public ResponseEntity<String> devRegister(@RequestBody CompleteSignupReq dto) {
        userService.signupVerified(dto); // 인증 없이 바로 등록
        return ResponseEntity.ok("테스트 계정 등록 완료");
    }

    /** 로그인 **/
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody UserDto dto,
            HttpServletRequest request // ← 추가
    ) {
        // 1) 인증 시도
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getEmail(),
                dto.getPassword());
        Authentication auth = authManager.authenticate(token);

        // 2) SecurityContextHolder에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 3) 세션 생성(또는 기존 세션 재사용) 후 SecurityContext 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        // 4) 빈 200 OK 응답만 내려주면 됩니다.
        return ResponseEntity.ok().build();
    }

    // 이하 send-verification, verify, register 메서드는 그대로 두시면 됩니다.
    @PostMapping("/send-verification")
    public ResponseEntity<Void> send(@RequestBody Map<String, String> body) {
        verifSvc.sendVerification(body.get("email"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public VerificationResponseDto verify(@RequestBody VerificationCodeDto dto) {
        return new VerificationResponseDto(
                verifSvc.verifyCode(dto.getEmail(), dto.getCode()));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CompleteSignupReq dto) {
        verifSvc.validateCodeForSignup(dto.getEmail(), dto.getCode());
        userService.signupVerified(dto);
        return ResponseEntity.ok("회원가입 성공!");
    }
}