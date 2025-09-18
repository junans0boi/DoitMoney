package com.hollywood.doitmoney.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteSignupReq {
    private String email; // 토큰 대신 이메일
    private String code; // 인증번호
    private String phone;
    private String password;
    private String username;
}