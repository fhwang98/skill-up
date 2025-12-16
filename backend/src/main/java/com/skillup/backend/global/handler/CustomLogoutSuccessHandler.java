package com.skillup.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.global.common.BaseResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("로그아웃 성공 : 쿠키 삭제");

        // RefreshToken 쿠키 제거
        Cookie deleteCookie = new Cookie("refreshToken", null);
        deleteCookie.setPath("/");
        deleteCookie.setHttpOnly(true);
        deleteCookie.setSecure(true);
        deleteCookie.setMaxAge(0);
        deleteCookie.setAttribute("SameSite", "None");

        response.addCookie(deleteCookie);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(BaseResponse.success(null))
        );
    }
}
