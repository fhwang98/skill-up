package com.skillup.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.domain.auth.service.RefreshTokenService;
import com.skillup.backend.global.common.BaseResponse;
import com.skillup.backend.global.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // username, role
        String username =  authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // JWT(Access/Refresh) 발급
        String accessToken = jwtUtil.createJWT(username, role, true);
        String refreshToken = jwtUtil.createJWT(username, role, false);

        // 발급한 Refresh redis 저장 (Refresh whitelist)
        refreshTokenService.addRefresh(username, refreshToken);

        // refreshToken 쿠키로 전달
        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);               // JS 접근 금지 → XSS 보호
        refreshCookie.setSecure(true);                 // HTTPS 환경에서만 전송
        refreshCookie.setPath("/");                    // 전체 경로에서 쿠키 전송
        refreshCookie.setMaxAge(60 * 60 * 24 * 14);    // 14일
        refreshCookie.setAttribute("SameSite", "None");

        response.addCookie(refreshCookie);

        // 응답
        // AccessToken만 BaseResponse로 반환
        Map<String, String> tokenMap = Map.of("accessToken", accessToken);

        BaseResponse<Map<String, String>> body = BaseResponse.success(tokenMap);

        // JSON 응답 설정
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(body));
        response.getWriter().flush();
    }
}