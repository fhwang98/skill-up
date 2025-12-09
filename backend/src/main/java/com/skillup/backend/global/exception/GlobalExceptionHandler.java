package com.skillup.backend.global.exception;

import com.skillup.backend.global.common.ApiResponse;
import com.skillup.backend.global.common.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = (fieldError != null) ? fieldError.getDefaultMessage() : "입력값 검증 실패";
        log.warn("ValidException: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        ErrorResponse.builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .code(ErrorCode.VALIDATION_ERROR.name())
                                .message(message)
                                .build()
                ));
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
