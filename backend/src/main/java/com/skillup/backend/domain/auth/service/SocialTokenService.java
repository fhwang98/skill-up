package com.skillup.backend.domain.auth.service;

import com.skillup.backend.domain.auth.entity.SocialTokenEntity;
import com.skillup.backend.domain.auth.repository.SocialTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialTokenService {

    private final SocialTokenRepository socialTokenRepository;

    public void addSocialToken(String email, String socialAccessToken) {
        SocialTokenEntity entity = SocialTokenEntity
                                        .builder()
                                        .email(email)
                                        .accessToken(socialAccessToken)
                                        .build();
        socialTokenRepository.save(entity);
    }
}
