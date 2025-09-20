package com.hollywood.doitmoney.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import com.hollywood.doitmoney.user.dto.VerificationResponseDto;
import com.hollywood.doitmoney.security.JwtUtil;
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
    private final JwtUtil jwtUtil;

    // 이메일 인증 없이 바로 가입하는 디버그용 API를 임시
    @PostMapping("/dev-register")
    public ResponseEntity<String> devRegister(@RequestBody CompleteSignupReq dto) {
        userService.signupVerified(dto); // 인증 없이 바로 등록
        return ResponseEntity.ok("테스트 계정 등록 완료");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserDto dto) {
        // 1. Spring Security를 통해 사용자 인증
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

        // 2. 인증 성공 시, 사용자 이메일을 기반으로 JWT 생성
        final String jwt = jwtUtil.generateToken(dto.getEmail());

        // 3. 생성된 토큰을 JSON 형태로 클라이언트에게 반환
        return ResponseEntity.ok(Map.of("token", jwt));
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