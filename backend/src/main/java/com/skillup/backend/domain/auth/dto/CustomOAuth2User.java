package com.skillup.backend.domain.auth.dto;

import com.skillup.backend.domain.user.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final UserEntity userEntity;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(UserEntity userEntity,
                            Map<String, Object> attributes,
                            Collection<? extends GrantedAuthority> authorities) {
        this.userEntity = userEntity;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() { return attributes; }

    @Override
    public String getName() { return userEntity.getEmail(); } // email 사용
}
