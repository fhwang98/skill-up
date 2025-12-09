package com.skillup.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.global.common.ApiResponse;
import com.skillup.backend.global.common.ErrorResponse;
import com.skillup.backend.global.exception.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ErrorCode code = ErrorCode.ACCESS_DENIED;

        ErrorResponse error = ErrorResponse.builder()
                .status(code.getStatus().value())
                .code(code.name())
                .message(code.getMessage())
                .build();

        ApiResponse<?> apiResponse = ApiResponse.error(error);

        response.setStatus(code.getStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(apiResponse));
    }
}
