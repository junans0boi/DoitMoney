// backend/src/main/java/com/hollywood/doitmoney/user/controller/UserController.java

package com.hollywood.doitmoney.user.controller;

import com.hollywood.doitmoney.security.CustomUserDetails;
import com.hollywood.doitmoney.user.dto.ChangePasswordReq;
import com.hollywood.doitmoney.user.dto.UpdateUserReq;
import com.hollywood.doitmoney.user.dto.UserProfileDto;
import com.hollywood.doitmoney.user.service.FileStorageService;
import com.hollywood.doitmoney.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FileStorageService fileStorageService;

    /** 이메일/전화번호 중복 체크 (인증 불필요) **/
    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        return Map.of("available", !userService.existsEmail(email));
    }

    @GetMapping("/check-phone")
    public Map<String, Boolean> checkPhone(@RequestParam String phone) {
        return Map.of("available", !userService.existsPhone(phone));
    }

    /** 내 프로필 조회 **/
    @GetMapping("/me")
    // 2. 파라미터를 @AuthenticationPrincipal CustomUserDetails userDetails 로 변경
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 3. 훨씬 안전하고 간결하게 userId 를 가져옵니다.
        Long userId = userDetails.getUserId();
        UserProfileDto dto = userService.getProfile(userId);
        return ResponseEntity.ok(dto);
    }

    /** 내 프로필 수정 (username + image) **/
    @PutMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserProfileDto> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 2. 변경
            @RequestPart("username") String username,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        Long userId = userDetails.getUserId(); // 3. 변경
        UpdateUserReq req = new UpdateUserReq();
        req.setUsername(username);
        if (image != null && !image.isEmpty()) {
            String url = fileStorageService.store(image);
            req.setProfileImageUrl(url);
        }
        UserProfileDto updated = userService.updateUser(userId, req);
        return ResponseEntity.ok(updated);
    }

    /** 내 비밀번호 변경 **/
    @PostMapping("/me/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 2. 변경
            @RequestBody ChangePasswordReq req) {
        Long userId = userDetails.getUserId(); // 3. 변경
        userService.changePassword(userId, req);
        return ResponseEntity.ok().build();
    }
}