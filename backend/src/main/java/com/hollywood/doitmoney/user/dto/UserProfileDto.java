// src/main/java/com/Planairy/backend/user/dto/UserProfileDto.java
package com.hollywood.doitmoney.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDto {
    private Long userId;
    private String email;
    private String username;
    private String phone;
    private String profileImageUrl;
    private long followingCount;
    private long followerCount;
}