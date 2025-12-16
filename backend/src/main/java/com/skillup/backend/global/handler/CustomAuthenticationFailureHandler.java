package com.skillup.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.global.common.BaseResponse;
import com.skillup.backend.global.common.ErrorResponse;
import com.skillup.backend.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler
        implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {

        ErrorCode code = ErrorCode.AUTHENTICATION_FAILED;

        if (exception instanceof OAuth2AuthenticationException e) {
            try {
                code = ErrorCode.valueOf(e.getError().getErrorCode());
            } catch (IllegalArgumentException ignored) {}
        } else if (exception instanceof UsernameNotFoundException) {
            code = ErrorCode.USER_NOT_FOUND;
        }

        String contextPath = request.getContextPath(); // "/api/v1"
        String uri = request.getRequestURI();          // "/api/v1/oauth2/authorization/kakao"
        // context-path 제거
        String path = uri.substring(contextPath.length());

        boolean isOAuth2Request =
                path.startsWith("/oauth2/")
                        || path.startsWith("/login/oauth2/");

        if (isOAuth2Request) {
            String redirectUrl =
                    "http://localhost:5173/login?error=" + code.name();
            response.sendRedirect(redirectUrl);
            return;
        }

        ErrorResponse error = ErrorResponse.builder()
                .status(code.getStatus().value())
                .code(code.name())
                .message(code.getMessage())
                .build();

        BaseResponse<?> baseResponse = BaseResponse.error(error);

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(baseResponse));
    }
}
