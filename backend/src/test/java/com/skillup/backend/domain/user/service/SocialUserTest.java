package com.skillup.backend.domain.user.service;

import com.skillup.backend.domain.auth.oauth.service.SocialUnlinkService;
import com.skillup.backend.domain.auth.service.RefreshTokenService;
import com.skillup.backend.domain.user.entity.SocialProviderType;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SocialUserTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private SocialUnlinkService socialUnlinkService;

    @Test
    @DisplayName("소셜 회원 탈퇴 성공 - KAKAO")
    void deleteUser_social_kakao_success() {
        // given
        String email = "social@test.com";

        UserEntity user = UserEntity.builder()
                .email(email)
                .provider(SocialProviderType.KAKAO)
                .deleted(false)
                .build();

        given(userRepository.findByEmailAndDeleted(email, false))
                .willReturn(Optional.of(user));

        // when
        userService.deleteUser(email, null);

        // then
        verify(socialUnlinkService).unlink(user);
        verify(refreshTokenService).removeRefresh(email);
        assertThat(user.isDeleted()).isTrue();
    }

}
