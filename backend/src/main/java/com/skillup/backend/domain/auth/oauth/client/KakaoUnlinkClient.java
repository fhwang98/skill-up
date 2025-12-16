package com.skillup.backend.domain.auth.oauth.client;

import com.skillup.backend.domain.auth.entity.SocialTokenEntity;
import com.skillup.backend.domain.auth.repository.SocialTokenRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoUnlinkClient {

    private final RestClient restClient = RestClient.builder().build();
    private final SocialTokenRepository socialTokenRepository;

    private final String KAKAO_UNLINK_URI = "https://kapi.kakao.com/v1/user/unlink";

    public void unlink(String email) {
        SocialTokenEntity entity = socialTokenRepository.findById(email).orElseThrow(() -> {
            log.warn("소셜 토큰 없음");
            return new CustomException(ErrorCode.SOCIAL_TOKEN_NOT_FOUND);
        });

        String accessToken = entity.getAccessToken();

        log.info("소셜로 accessToken unlink 시도");
        Map<String, Long> response = restClient.post()
                .uri(KAKAO_UNLINK_URI)
                .headers(headers -> {
                    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
                    headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
                })
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        (req, res) -> {
                            log.error("카카오 unlink HTTP 실패 status={}", res.getStatusCode());
                            throw new CustomException(ErrorCode.SOCIAL_UNLINK_FAILED);
                        }
                )
                .body(Map.class);

        Long id = response.get("id");
        if (id == null) {
            log.error("카카오 unlink 실패 response={}", response);
            throw new CustomException(ErrorCode.SOCIAL_UNLINK_FAILED);
        }
        log.info("카카오 unlink 성공 id={}", id);
        socialTokenRepository.deleteById(email);

    }
}
