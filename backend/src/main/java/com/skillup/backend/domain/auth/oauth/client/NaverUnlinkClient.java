package com.skillup.backend.domain.auth.oauth.client;

import com.skillup.backend.domain.auth.entity.SocialTokenEntity;
import com.skillup.backend.domain.auth.repository.SocialTokenRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverUnlinkClient {

    private final RestClient restClient = RestClient.builder().build();
    private final SocialTokenRepository socialTokenRepository;

    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String naverTokenUri;
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;


    public void unlink(String email) {

        SocialTokenEntity entity = socialTokenRepository.findById(email).orElseThrow(() -> {
            log.warn("소셜 토큰 없음");
            return new CustomException(ErrorCode.SOCIAL_TOKEN_NOT_FOUND);
        });

        String accessToken = entity.getAccessToken();

        log.info("소셜로 accessToken unlink 시도");
        Map<String, String> response = restClient.post()
                .uri(naverTokenUri
                        + "?grant_type=delete"
                        + "&client_id=" + naverClientId
                        + "&client_secret=" + naverClientSecret
                        + "&access_token=" + accessToken)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        (req, res) -> {
                            log.error("네이버 unlink HTTP 실패 status={}", res.getStatusCode());
                            throw new CustomException(ErrorCode.SOCIAL_UNLINK_FAILED);
                        }
                )
                .body(Map.class);

        String result = response.get("result");

        if (!"success".equals(result)) {
            log.error("네이버 unlink 실패 response={}", response);
            throw new CustomException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }

        log.info("네이버 unlink 성공 소셜 토큰 삭제");
        socialTokenRepository.deleteById(email);
    }
}
