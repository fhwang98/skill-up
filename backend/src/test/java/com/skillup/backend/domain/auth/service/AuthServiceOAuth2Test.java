package com.skillup.backend.domain.auth.service;

import com.skillup.backend.domain.auth.dto.CustomOAuth2User;
import com.skillup.backend.domain.user.entity.SocialProviderType;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.entity.UserRoleType;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceOAuth2Test {

    @Mock
    private UserRepository userRepository;

    @Spy
    @InjectMocks
    private AuthService authService;

    @DisplayName("소셜 로그인 - 카카오 신규 사용자 생성")
    @Test
    void loadUser_kakao_newUser() {
        // given
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration registration = mock(ClientRegistration.class);

        given(userRequest.getClientRegistration()).willReturn(registration);
        given(registration.getRegistrationId()).willReturn("kakao");

        Map<String, Object> attributes = Map.of(
                "id", 12345L,
                "kakao_account", Map.of(
                        "email", "kakao@test.com",
                        "profile", Map.of("nickname", "카카오유저")
                )
        );

        OAuth2User oAuth2User = mock(OAuth2User.class);
        given(oAuth2User.getAttributes()).willReturn(attributes);

        // super.loadUser 대체
        doReturn(oAuth2User)
                .when(authService)
                .loadOAuth2User(any(OAuth2UserRequest.class));

        given(userRepository.findByEmailAndDeleted("kakao@test.com", false))
                .willReturn(Optional.empty());

        // when
        OAuth2User result = authService.loadUser(userRequest);

        // then
        assertThat(result).isInstanceOf(CustomOAuth2User.class);

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("kakao@test.com");
        assertThat(savedUser.getProvider()).isEqualTo(SocialProviderType.KAKAO);
        assertThat(savedUser.getRole()).isEqualTo(UserRoleType.ROLE_USER);
    }

    @DisplayName("소셜 로그인 - 기존 사용자면 업데이트만 수행")
    @Test
    void loadUser_kakao_existingUser() {
        // given
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration registration = mock(ClientRegistration.class);

        given(userRequest.getClientRegistration()).willReturn(registration);
        given(registration.getRegistrationId()).willReturn("kakao");

        Map<String, Object> attributes = Map.of(
                "id", 99999L,
                "kakao_account", Map.of(
                        "email", "exist@test.com",
                        "profile", Map.of("nickname", "새닉네임")
                )
        );

        OAuth2User oAuth2User = mock(OAuth2User.class);
        given(oAuth2User.getAttributes()).willReturn(attributes);

        doReturn(oAuth2User)
                .when(authService)
                .loadOAuth2User(any());

        UserEntity existingUser = UserEntity.builder()
                .email("exist@test.com")
                .nickname("이전닉네임")
                .provider(SocialProviderType.KAKAO)
                .providerId("old-id")
                .role(UserRoleType.ROLE_USER)
                .deleted(false)
                .build();

        given(userRepository.findByEmailAndDeleted("exist@test.com", false))
                .willReturn(Optional.of(existingUser));

        // when
        OAuth2User result = authService.loadUser(userRequest);

        // then
        assertThat(result).isInstanceOf(CustomOAuth2User.class);
        verify(userRepository).save(existingUser);
    }

    @DisplayName("소셜 로그인 - 지원하지 않는 provider면 예외 발생")
    @Test
    void loadUser_invalidProvider() {
        // given
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        ClientRegistration registration = mock(ClientRegistration.class);

        given(userRequest.getClientRegistration()).willReturn(registration);
        given(registration.getRegistrationId()).willReturn("google"); // 미지원

        OAuth2User oAuth2User = mock(OAuth2User.class);
        given(oAuth2User.getAttributes()).willReturn(Map.of());

        doReturn(oAuth2User)
                .when(authService)
                .loadOAuth2User(any());

        // when / then
        assertThatThrownBy(() -> authService.loadUser(userRequest))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PROVIDER);
    }

}
