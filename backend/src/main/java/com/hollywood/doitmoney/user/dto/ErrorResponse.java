package com.hollywood.doitmoney.user.dto;

// record는 Java 16+ 부터 사용 가능한 불변 데이터 객체입니다.
// 생성자, getter, equals, hashCode, toString을 자동으로 만들어줍니다.
public record ErrorResponse(String message) {
}