package com.skillup.backend.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.global.common.BaseResponse;
import com.skillup.backend.global.common.ErrorResponse;
import com.skillup.backend.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
