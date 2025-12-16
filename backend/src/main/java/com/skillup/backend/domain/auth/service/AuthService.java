package com.skillup.backend.domain.auth.service;

import com.skillup.backend.domain.auth.dto.CustomOAuth2User;
import com.skillup.backend.domain.auth.dto.CustomUserDetails;
import com.skillup.backend.domain.user.entity.SocialProviderType;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.entity.UserRoleType;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // 자체로그인
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("자체 로그인 email: {}", email);
        UserEntity user = userRepository.findByEmailAndDeleted(email, false)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", email);
                    return new UsernameNotFoundException("USER_NOT_FOUND");
                });
        return new CustomUserDetails(user);
    }

    // 소셜 로그인
    protected OAuth2User loadOAuth2User(OAuth2UserRequest userRequest) {
        return super.loadUser(userRequest);
    }
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("소셜 로그인 요청: {}", userRequest.getClientRegistration().getRegistrationId());

        OAuth2User oAuth2User = loadOAuth2User(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String nickname;
        String providerId;

        // 정보 파싱
        if (registrationId.equals(SocialProviderType.NAVER.name())) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            providerId = response.get("id").toString();
            email = response.get("email").toString();
            nickname = response.getOrDefault("nickname", response.get("name")).toString();
        } else if (registrationId.equals(SocialProviderType.KAKAO.name())) {
            providerId = attributes.get("id").toString();
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = kakaoAccount.get("email").toString();
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            nickname = profile.get("nickname").toString();
        } else {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            ErrorCode.INVALID_PROVIDER.name(),
                            ErrorCode.INVALID_PROVIDER.getMessage(),
                            null
                    )
            );

        }

        UserEntity user = userRepository.findByEmailAndDeleted(email, false)
                .orElse(null);

        if (user == null) {
            // 신규 가입
            user = UserEntity.builder()
                    .email(email)
                    .password("")
                    .nickname(nickname)
                    .provider(SocialProviderType.valueOf(registrationId))
                    .providerId(providerId)
                    .role(UserRoleType.ROLE_USER)
                    .deleted(false)
                    .build();
        } else {
            // 이미 가입된 이메일
            if (!user.getProvider().name().equals(registrationId)) {
                log.warn("이미 다른 소셜로 가입된 이메일: {}", email);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(ErrorCode.DUPLICATE_EMAIL.name(),
                                ErrorCode.DUPLICATE_EMAIL.getMessage(),
                                null));
            }
        }


        // 기존 유저면 변경된 정보?? 업데이트
        user.updateSocialUser(email, nickname, providerId);

        userRepository.save(user);

        // CustomOAuth2User 반환
        List<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(user.getRole().name()));

        return new CustomOAuth2User(user, attributes, authorities);
    }

}
