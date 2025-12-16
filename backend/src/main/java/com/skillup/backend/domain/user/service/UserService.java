package com.skillup.backend.domain.user.service;

import com.skillup.backend.domain.auth.oauth.service.SocialUnlinkService;
import com.skillup.backend.domain.auth.service.RefreshTokenService;
import com.skillup.backend.domain.user.dto.UserRequestDTO;
import com.skillup.backend.domain.user.dto.UserResponseDTO;
import com.skillup.backend.domain.user.entity.SocialProviderType;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.entity.UserRoleType;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final SocialUnlinkService socialUnlinkService;

    // 자체 회원가입
    @Transactional
    public Long createUser(UserRequestDTO dto) {

        log.info("회원가입 요청 - 이메일: {}, 닉네임:{}", dto.getEmail(), dto.getNickname());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("이미 존재하는 이메일: {}", dto.getEmail());
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            log.warn("이미 존재하는 닉네임: {}", dto.getNickname());
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        UserEntity entity = UserEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .role(UserRoleType.ROLE_USER) // 일반 유저로 가입
                .provider(SocialProviderType.LOCAL) // 자체 회원가입
                .build();

        return userRepository.save(entity).getId();

    }

    // 회원가입 이메일 중복 검사
    @Transactional(readOnly = true)
    public Boolean existsByEmail(UserRequestDTO dto) {
        log.info("이메일 중복 검사 요청: {}", dto.getEmail());
        return userRepository.existsByEmail(dto.getEmail());
    }

    // 회원가입 닉네임 중복 검사
    @Transactional(readOnly = true)
    public Boolean existsByNickname(UserRequestDTO dto) {
        log.info("닉네임 중복 검사 요청: {}", dto.getNickname());
        return userRepository.existsByNickname(dto.getNickname());
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDTO getByEmail(String email) {
        log.info("회원 정보 조회 email: {}", email);
        UserEntity entity = userRepository.findByEmailAndDeleted(email, false)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });
        return UserResponseDTO
                .builder()
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .provider(entity.getProvider().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // 회원 정보 수정
    @Transactional
    public Long updateUser(String email, UserRequestDTO dto) {

        log.info("회원정보 수정 요청 email: {}", email);
        UserEntity entity = userRepository.findByEmailAndDeleted(email, false)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });
        // 자체 회원만 수정 가능
        if(!entity.getProvider().equals(SocialProviderType.LOCAL)) {
            log.warn("소셜 회원 정보 수정 불가");
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        log.info("기존 닉네임: {} , 변경 닉네임: {}", entity.getNickname(), dto.getNickname());

        entity.updateUser(dto.getNickname());

        return entity.getId();
    }

    //  비밀번호 변경 요청
    @Transactional
    public Long updateUserPassword(String email, UserRequestDTO dto) {
        log.info("비밀번호 변경 요청 email: {}", email);
        UserEntity entity = userRepository.findByEmailAndDeleted(email, false)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });
        // 자체 회원만 변경 가능
        if(!entity.getProvider().equals(SocialProviderType.LOCAL)) {
            log.warn("소셜 회원 정보 수정 불가");
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        // 현재 비밀번호가 일치하지 않음
        if (!passwordEncoder.matches(dto.getPassword(), entity.getPassword())) {
            log.warn("비밀번호 일치하지 않음");
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        // 현재 비밀번호와 새로운 비밀번호가 일치함
        if (dto.getPassword().equals(dto.getNewPassword())) {
            log.warn("기존 비밀번호와 새로운 비밀번호 일치");
            throw new CustomException(ErrorCode.SAME_AS_OLD_PASSWORD);
        }

        entity.updatePassword(passwordEncoder.encode(dto.getNewPassword()));

        return entity.getId();
    }

    @Transactional
    public void deleteUser(String email, UserRequestDTO dto) {
        log.info("회원탈퇴 요청 :{}", email);
        UserEntity entity = userRepository.findByEmailAndDeleted(email, false)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        // 자체회원 -> 토큰 지우기 & 소프트 딜리트
        // 소셜회원 -> 토큰 지우기 & 소프트 딜리트 & 소셜토큰지우기 & 소셜에 토큰 해제 요청

        if (entity.getProvider().equals(SocialProviderType.LOCAL)) {
            // 자체 회원 비밀번호 확인
            if (!passwordEncoder.matches(dto.getPassword(), entity.getPassword())) {
                log.warn("비밀번호 일치하지 않음");
                throw new CustomException(ErrorCode.INVALID_PASSWORD);
            }
        }
        else {
            // 소셜 회원 처리
            log.info("소셜 회원 탈퇴 - provider: {}", entity.getProvider());
            // 소셜 연동 해제
            socialUnlinkService.unlink(entity);
        }

        // 토큰 지우기
        refreshTokenService.removeRefresh(email);

        // 유저 soft delete
        entity.delete();

        log.info("회원탈퇴 완료 email: {}", email);

    }
}
