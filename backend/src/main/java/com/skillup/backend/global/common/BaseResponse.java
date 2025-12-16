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
@Schema(description = "공통 API 응답 wrapper")
public class BaseResponse<T> {

    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success;
    @Schema(description = "응답 데이터", nullable = true)
    private T data;
    @Schema(description = "오류 정보", nullable = true)
    private ErrorResponse error;

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(true, data, null);
    }
    public static <T> BaseResponse<T> error(ErrorResponse error) {
        return new BaseResponse<>(false, null, error);
    }
}
