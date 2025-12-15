package com.skillup.backend.domain.user.service;

import com.skillup.backend.domain.user.dto.UserRequestDTO;
import com.skillup.backend.domain.user.dto.UserResponseDTO;
import com.skillup.backend.domain.user.entity.SocialProviderType;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.entity.UserRoleType;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@test.com")
                .password("password")
                .nickname("테스터")
                .build();

        // when
        Long userId = userService.createUser(dto);

        // then
        assertThat(userId).isNotNull();

        UserEntity savedUser = userRepository.findById(userId).orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
        assertThat(passwordEncoder.matches(dto.getPassword(), savedUser.getPassword())).isTrue();
        assertThat(savedUser.isDeleted()).isFalse();
        assertThat(savedUser.getRole()).isEqualTo(UserRoleType.ROLE_USER);
    }

    @Test
    @DisplayName("중복 이메일 - 회원가입 실패")
    void signup_duplicateEmail_fail() {
        // given
        UserRequestDTO dto1 = UserRequestDTO.builder()
                .email("dup@test.com")
                .password("pass")
                .nickname("홍길동")
                .build();

        UserRequestDTO dto2 = UserRequestDTO.builder()
                .email("dup@test.com")
                .password("pass")
                .nickname("둘리")
                .build();

        userService.createUser(dto1);

        // when & then
        assertThatThrownBy(() -> userService.createUser(dto2))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("중복 닉네임 - 회원가입 실패")
    void signup_duplicateNickname_fail() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("a@test.com")
                        .password("pass")
                        .nickname("닉네임")
                        .build()
        );

        UserRequestDTO dto = UserRequestDTO.builder()
                .email("b@test.com")
                .password("pass")
                .nickname("닉네임")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.createUser(dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("이메일 중복 검사 - 중복됨")
    void existEmail_ReturnsTrue_WhenEmailExists() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("dup@test.com")
                        .password("pass")
                        .nickname("홍길동")
                        .build()
        );

        UserRequestDTO dto = UserRequestDTO.builder()
                .email("dup@test.com")
                .build();

        // when
        boolean exists = userService.existsByEmail(dto);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임 중복 검사 - 중복됨")
    void existNickname_ReturnsTrue_WhenNicknameExists() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("dup@test.com")
                        .password("pass")
                        .nickname("닉네임")
                        .build()
        );

        UserRequestDTO dto = UserRequestDTO.builder()
                .nickname("닉네임")
                .build();

        // when
        boolean exists = userService.existsByNickname(dto);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    void getUser_success() {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("me@test.com")
                .password("password")
                .nickname("조회유저")
                .build();

        userService.createUser(dto);

        // when
        UserResponseDTO response = userService.getByEmail("me@test.com");

        // then
        assertThat(response.getEmail()).isEqualTo("me@test.com");
        assertThat(response.getNickname()).isEqualTo("조회유저");
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 사용자")
    void getUser_userNotFound_fail() {
        // when & then
        assertThatThrownBy(() -> userService.getByEmail("no@test.com"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 - LOCAL 회원 닉네임 변경")
    void updateUser_success() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("update@test.com")
                        .password("password")
                        .nickname("기존닉네임")
                        .build()
        );

        UserRequestDTO updateDto = UserRequestDTO.builder()
                .nickname("변경닉네임")
                .build();

        // when
        Long updatedId = userService.updateUser("update@test.com", updateDto);

        // then
        UserEntity updatedUser = userRepository.findById(updatedId).orElseThrow();
        assertThat(updatedUser.getNickname()).isEqualTo("변경닉네임");
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 사용자")
    void updateUser_userNotFound_fail() {
        // given
        UserRequestDTO updateDto = UserRequestDTO.builder()
                .nickname("변경닉네임")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updateUser("no@test.com", updateDto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
    @Test
    @DisplayName("회원 정보 수정 실패 - 소셜 회원은 수정 불가")
    void updateUser_socialUser_fail() {
        // given
        userRepository.save(
                UserEntity.builder()
                        .email("social@test.com")
                        .password("")
                        .nickname("소셜닉네임")
                        .provider(SocialProviderType.KAKAO)
                        .role(UserRoleType.ROLE_USER)
                        .deleted(false)
                        .build());

        UserRequestDTO updateDto = UserRequestDTO.builder()
                .nickname("변경닉네임")
                .build();

        // when & then
        assertThatThrownBy(() -> userService.updateUser("social@test.com", updateDto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }
}
