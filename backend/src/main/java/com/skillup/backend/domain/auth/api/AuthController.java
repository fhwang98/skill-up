package com.skillup.backend.domain.auth.api;

import com.skillup.backend.domain.auth.service.RefreshTokenService;
import com.skillup.backend.global.common.BaseResponse;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@Tag(name = "AUTH API", description = "사용자 인증 관련 api")
@RestController(value = "/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RefreshTokenService refreshTokenService;

    @PostMapping(value = "/refresh")
    @Operation(summary = "토큰 재발급", description = "refreshCookie 기반 acessToken을 재발급합니다.")
    public ResponseEntity<BaseResponse<Map<String, String>>> tokenRefresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        String newAccessToken = refreshTokenService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(
                BaseResponse.success(Map.of("accessToken", newAccessToken))
        );
    }

}
