package com.skillup.backend.domain.auth.service;

import com.skillup.backend.domain.auth.entity.RefreshTokenEntity;
import com.skillup.backend.domain.auth.repository.RefreshTokenRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import com.skillup.backend.global.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;

    // JWT Refresh 토큰 발급 후 저장 메소드
    public void addRefresh(String email, String refreshToken) {
        RefreshTokenEntity entity = RefreshTokenEntity.builder()
                .email(email)
                .refreshToken(refreshToken)
                .build();

        refreshTokenRepository.save(entity);
        log.info("[Redis] RefreshToken 저장 완료 => email={}, token={}", email, refreshToken);
    }

    // JWT Refresh 토큰 삭제 메소드
    public void removeRefresh(String email) {

        refreshTokenRepository.deleteById(email);
        log.info("[Redis] RefreshToken 삭제 완료 (email={})", email);
    }
    // AccessToken 재발급
    public String refreshAccessToken(String refreshToken) {

        log.info("accessToken 재발급");

        // RefreshToken JWT 검증
        boolean isValid = jwtUtil.isValid(refreshToken, false);
        if (!isValid) {
            log.warn("유효하지 않은 토큰");
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 2️⃣ 토큰에서 사용자 정보 추출
        String email = jwtUtil.getUsername(refreshToken);
        String role = jwtUtil.getRole(refreshToken);

        // Redis whitelist 조회 (ID 기반)
        RefreshTokenEntity entity =
                refreshTokenRepository.findById(email)
                        .orElseThrow(() -> {
                            log.warn("RefreshToken 없음 (email={})", email);
                            return new CustomException(ErrorCode.INVALID_TOKEN);
                        });

        // 토큰 일치 여부 검증
        if (!entity.getRefreshToken().equals(refreshToken)) {
            log.warn("RefreshToken 불일치 (email={})", email);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 새 AccessToken 발급
        return jwtUtil.createJWT(email, role, true);
    }

}
