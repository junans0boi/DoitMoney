package com.hollywood.doitmoney.config;

import com.hollywood.doitmoney.user.dto.ErrorResponse;
import com.hollywood.doitmoney.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 모든 @RestController에서 발생하는 예외를 처리합니다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // UserNotFoundException 타입의 예외가 발생하면 이 메소드가 실행됩니다.
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(ex.getMessage());
        // 404 NOT FOUND 상태 코드와 함께 에러 메시지를 JSON 형태로 반환합니다.
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}