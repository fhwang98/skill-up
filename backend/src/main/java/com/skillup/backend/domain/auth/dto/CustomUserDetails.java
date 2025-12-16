package com.skillup.backend.domain.auth.dto;

import com.skillup.backend.domain.user.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UserEntity userEntity;

    public CustomUserDetails(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    // 권한
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userEntity.getRole().name()));
    }

    // 패스워드
    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    // username = email 로 사용
    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    // 계정 상태 – 지금은 전부 true로 둠
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return !userEntity.isDeleted(); }
}
