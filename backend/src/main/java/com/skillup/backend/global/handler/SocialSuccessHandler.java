package com.skillup.backend.global.handler;

import com.skillup.backend.domain.auth.service.RefreshTokenService;
import com.skillup.backend.domain.auth.service.SocialTokenService;
import com.skillup.backend.global.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocialSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final SocialTokenService socialTokenService;
    private final RefreshTokenService refreshTokenService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        String username =  authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        log.info("소셜 로그인 성공 :{}", username);
        OAuth2AuthorizedClient client =
                authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName()
                );
        String socialAccessToken = client.getAccessToken().getTokenValue();

        log.info("소셜 액세스 토큰 저장 / username={}", username);
        socialTokenService.addSocialToken(username, socialAccessToken);

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

        String redirectUrl =
                "http://localhost:5173/oauth2/callback?accessToken=" + accessToken;

        response.sendRedirect(redirectUrl);

    }
}
