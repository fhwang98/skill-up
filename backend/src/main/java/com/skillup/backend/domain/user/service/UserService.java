package com.skillup.backend.domain.user.service;

import com.skillup.backend.domain.user.dto.UserRequestDTO;
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


}
