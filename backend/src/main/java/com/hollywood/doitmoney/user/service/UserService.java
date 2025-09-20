package com.hollywood.doitmoney.user.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hollywood.doitmoney.user.dto.*;
import com.hollywood.doitmoney.user.entity.User;
import com.hollywood.doitmoney.user.repository.UserRepository;
import com.hollywood.doitmoney.user.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder enc;

    private static final String DEFAULT_PROFILE_IMAGE = "https://blog.kakaocdn.net/dn/4CElL/btrQw18lZMc/Q0oOxqQNdL6kZp0iSKLbV1/img.png";

    /* 중복 검사 API에서 사용 */
    public boolean existsEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public boolean existsPhone(String phone) {
        return userRepository.findByPhone(phone).isPresent();
    }

    /** 이메일 인증 후 최종 가입 */
    /** 이메일 인증 후 최종 가입 */
    public void signupVerified(CompleteSignupReq dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다.");
        if (userRepository.existsByPhone(dto.getPhone()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 등록된 전화번호입니다.");

        User u = User.builder()
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .password(enc.encode(dto.getPassword()))
                .username(dto.getUsername())
                .profileImageUrl(DEFAULT_PROFILE_IMAGE)
                .build();
        userRepository.save(u);
    }

    /* 로그인 */
    public Optional<User> login(String email, String rawPwd) {
        return userRepository.findByEmail(email)
                .filter(u -> enc.matches(rawPwd, u.getPassword()));
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserProfileDto updateUser(Long id, UpdateUserReq req) {
        User u = userRepository.findById(id) // userId -> id
                .orElseThrow(() -> new UserNotFoundException("ID가 " + id + "인 회원을 찾을 수 없습니다.")); // userId -> id

        u.setUsername(req.getUsername());
        u.setProfileImageUrl(req.getProfileImageUrl());
        userRepository.save(u);
        return new UserProfileDto(
                u.getUserId(), u.getEmail(), u.getUsername(),
                u.getPhone(), u.getProfileImageUrl(),
                countFollowing(id), countFollowers(id));
    }

    public Optional<UserProfileDto> loginAndGetProfile(String email, String rawPwd) {
        return login(email, rawPwd).map(u -> getProfile(u.getUserId()));
    }

    public UserProfileDto getProfile(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 회원을 찾을 수 없습니다."));
        return new UserProfileDto(
                u.getUserId(), u.getEmail(), u.getUsername(),
                u.getPhone(), u.getProfileImageUrl(),
                countFollowing(userId), countFollowers(userId));
    }

    /** 비밀번호 변경 **/
    public void changePassword(Long userId, ChangePasswordReq req) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 회원을 찾을 수 없습니다."));
        if (!enc.matches(req.getOldPassword(), u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "기존 비밀번호가 일치하지 않습니다.");
        }
        u.setPassword(enc.encode(req.getNewPassword()));
        userRepository.save(u);
    }

    /** 팔로잉/팔로워 카운트 (현재는 0) **/
    public long countFollowing(Long userId) {
        return 0;
    }

    public long countFollowers(Long userId) {
        return 0;
    }
}