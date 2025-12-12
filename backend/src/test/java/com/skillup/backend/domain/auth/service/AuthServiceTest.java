package com.skillup.backend.domain.auth.service;

import com.skillup.backend.domain.auth.dto.CustomUserDetails;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.entity.UserRoleType;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @DisplayName("자체 로그인 - 사용자 존재 시 CustomUserDetails 반환")
    @Test
    void loadUserByUsername_success() {
        // given
        UserEntity user = UserEntity.builder()
                .email("test@test.com")
                .password("encodedPw")
                .nickname("tester")
                .role(UserRoleType.ROLE_USER)
                .deleted(false)
                .build();

        given(userRepository.findByEmailAndDeleted("test@test.com", false))
                .willReturn(Optional.of(user));

        // when
        UserDetails result = authService.loadUserByUsername("test@test.com");

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        assertThat(result.getUsername()).isEqualTo("test@test.com");
        assertThat(result.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_USER");
    }

    @DisplayName("자체 로그인 - 사용자 없으면 UsernameNotFoundException 발생")
    @Test
    void loadUserByUsername_fail() {
        // given
        given(userRepository.findByEmailAndDeleted(anyString(), anyBoolean()))
                .willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> authService.loadUserByUsername("no@test.com"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
}
