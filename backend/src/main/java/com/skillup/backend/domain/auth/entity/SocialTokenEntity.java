package com.skillup.backend.domain.auth.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@RedisHash(value = "social_token", timeToLive = 60 * 60)
public class SocialTokenEntity {

    @Id
    private String email;

    private String accessToken;
}
