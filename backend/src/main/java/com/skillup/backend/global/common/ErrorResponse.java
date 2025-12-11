package com.skillup.backend.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;
    @Schema(description = "에러 코드", example = "VALIDATION_ERROR")
    private String code;
    @Schema(description = "에러 메시지", example = "입력값 검증 실패")
    private String message;
}
