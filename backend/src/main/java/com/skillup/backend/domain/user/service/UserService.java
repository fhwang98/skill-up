package com.skillup.backend.domain.user.service;

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



}
