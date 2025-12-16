package com.skillup.backend.domain.auth.oauth.service;

import com.skillup.backend.domain.auth.oauth.client.KakaoUnlinkClient;
import com.skillup.backend.domain.auth.oauth.client.NaverUnlinkClient;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SocialUnlinkService {
    private final NaverUnlinkClient naverUnlinkClient;
    private final KakaoUnlinkClient kakaoUnlinkClient;
    // 소셜 회원탈퇴
    public void unlink(UserEntity entity) {
        switch (entity.getProvider()) {
            case NAVER -> naverUnlinkClient.unlink(entity.getEmail());
            case KAKAO -> kakaoUnlinkClient.unlink(entity.getEmail());
            default -> throw new CustomException(ErrorCode.INVALID_PROVIDER);
        };
    }
}
