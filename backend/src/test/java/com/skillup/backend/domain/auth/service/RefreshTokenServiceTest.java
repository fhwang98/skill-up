package com.skillup.backend.domain.auth.service;

import com.skillup.backend.domain.auth.entity.RefreshTokenEntity;
import com.skillup.backend.domain.auth.repository.RefreshTokenRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import com.skillup.backend.global.util.JWTUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JWTUtil jwtUtil;

    private final String email = "test@test.com";
    private final String role = "ROLE_USER";
    private final String refreshToken = "valid-refresh-token";

    @Test
    @DisplayName("addRefresh - RefreshToken 저장")
    void addRefresh_success() {
        // when
        refreshTokenService.addRefresh(email, refreshToken);

        // then
        verify(refreshTokenRepository).save(
                argThat(entity ->
                        entity.getEmail().equals(email) &&
                                entity.getRefreshToken().equals(refreshToken)
                )
        );
    }

    @Test
    @DisplayName("removeRefresh - RefreshToken 삭제")
    void removeRefresh_success() {
        // when
        refreshTokenService.removeRefresh(email);

        // then
        verify(refreshTokenRepository).deleteById(email);
    }

    @Test
    @DisplayName("refreshAccessToken 성공 - 정상 RefreshToken")
    void refreshAccessToken_success() {
        // given
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();

        when(jwtUtil.isValid(refreshToken, false)).thenReturn(true);
        when(jwtUtil.getUsername(refreshToken)).thenReturn(email);
        when(jwtUtil.getRole(refreshToken)).thenReturn(role);
        when(refreshTokenRepository.findById(email))
                .thenReturn(Optional.of(entity));
        when(jwtUtil.createJWT(email, role, true))
                .thenReturn("new-access-token");

        // when
        String newAccessToken =
                refreshTokenService.refreshAccessToken(refreshToken);

        // then
        assertThat(newAccessToken).isEqualTo("new-access-token");
        verify(jwtUtil).createJWT(email, role, true);
    }

    @Test
    @DisplayName("refreshAccessToken 실패 - JWT 검증 실패")
    void refreshAccessToken_invalidJwt() {
        // given
        when(jwtUtil.isValid(refreshToken, false)).thenReturn(false);

        // when / then
        assertThatThrownBy(() ->
                refreshTokenService.refreshAccessToken(refreshToken)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);

        verify(refreshTokenRepository, never()).findById(any());
    }

    @Test
    @DisplayName("refreshAccessToken 실패 - Redis에 RefreshToken 없음")
    void refreshAccessToken_notFoundInRedis() {
        // given
        when(jwtUtil.isValid(refreshToken, false)).thenReturn(true);
        when(jwtUtil.getUsername(refreshToken)).thenReturn(email);
        when(jwtUtil.getRole(refreshToken)).thenReturn(role);
        when(refreshTokenRepository.findById(email))
                .thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() ->
                refreshTokenService.refreshAccessToken(refreshToken)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("refreshAccessToken 실패 - 저장된 RefreshToken 불일치")
    void refreshAccessToken_tokenMismatch() {
        // given
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .email(email)
                .refreshToken("different-refresh-token")
                .build();

        when(jwtUtil.isValid(refreshToken, false)).thenReturn(true);
        when(jwtUtil.getUsername(refreshToken)).thenReturn(email);
        when(jwtUtil.getRole(refreshToken)).thenReturn(role);
        when(refreshTokenRepository.findById(email))
                .thenReturn(Optional.of(entity));

        // when / then
        assertThatThrownBy(() ->
                refreshTokenService.refreshAccessToken(refreshToken)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TOKEN);
    }
}
