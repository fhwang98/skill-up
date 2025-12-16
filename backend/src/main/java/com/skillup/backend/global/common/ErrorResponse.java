package com.skillup.backend.global.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "HTTP 상태 코드", example = "400")
    private int status;
    @Schema(description = "에러 코드", example = "VALIDATION_ERROR")
    private String code;
    @Schema(description = "에러 메시지", example = "입력값 검증 실패")
    private String message;
}
