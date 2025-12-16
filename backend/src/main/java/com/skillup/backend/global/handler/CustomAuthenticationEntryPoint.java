package com.skillup.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.global.common.BaseResponse;
import com.skillup.backend.global.common.ErrorResponse;
import com.skillup.backend.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ErrorCode code = ErrorCode.UNAUTHORIZED;

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
