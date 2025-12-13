package com.skillup.backend.global.handler;

import com.skillup.backend.domain.auth.service.RefreshTokenService;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("로그아웃 요청");
        if (authentication == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String email = authentication.getName();

        // RefreshToken 삭제
        refreshTokenService.removeRefresh(email);
    }
}
