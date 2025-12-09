package com.skillup.backend.global.exception;

import com.skillup.backend.global.common.ApiResponse;
import com.skillup.backend.global.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();

        log.warn("CustomException: {}", code.getMessage());

        return buildErrorResponse(code.getStatus(), code.name(), code.getMessage());
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponse(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(
                        ErrorResponse.builder()
                                .status(status.value())
                                .code(code)
                                .message(message)
                                .build()
                ));
    }
}
