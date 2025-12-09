package com.skillup.backend.global.common;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String code;
    private String message;
}
