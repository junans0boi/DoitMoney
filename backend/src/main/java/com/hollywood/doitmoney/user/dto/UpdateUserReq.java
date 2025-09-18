// src/main/java/com/Planairy/backend/user/dto/UpdateUserReq.java
package com.hollywood.doitmoney.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserReq {
    private String username;
    private String profileImageUrl;
}