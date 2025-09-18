// src/main/java/com/doitmoney/backend/user/dto/ChangePasswordReq.java
package com.hollywood.doitmoney.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordReq {
    private String oldPassword;
    private String newPassword;
}