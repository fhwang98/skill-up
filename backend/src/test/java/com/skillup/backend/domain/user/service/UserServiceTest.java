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

    @Test
    @DisplayName("비밀번호 변경 테스트")
    void updatePassword_success() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("pw@test.com")
                        .password("password")
                        .nickname("닉네임")
                        .build()
        );

        UserRequestDTO passwodDto = UserRequestDTO.builder()
                .password("password")
                .newPassword("changedpw")
                .build();

        // when
        Long updatedId = userService.updateUserPassword("pw@test.com", passwodDto);

        // then
        UserEntity updatedUser = userRepository.findById(updatedId).orElseThrow();
        // 기존 비밀번호는 더 이상 맞지 않아야 함
        assertThat(passwordEncoder.matches("password", updatedUser.getPassword()))
                .isFalse();

        // 새 비밀번호는 정상적으로 매칭되어야 함
        assertThat(passwordEncoder.matches("changedpw", updatedUser.getPassword()))
                .isTrue();

    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 존재하지 않는 사용자")
    void updatePassword_userNotFound_fail() {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .password("password")
                .newPassword("changedpw")
                .build();

        // when & then
        assertThatThrownBy(() ->
                userService.updateUserPassword("no@test.com", dto)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }
    @Test
    @DisplayName("비밀번호 변경 실패 - 소셜 회원")
    void updatePassword_socialUser_fail() {
        // given
        userRepository.save(
                UserEntity.builder()
                        .email("social@test.com")
                        .password("")
                        .nickname("소셜유저")
                        .provider(SocialProviderType.KAKAO)
                        .role(UserRoleType.ROLE_USER)
                        .deleted(false)
                        .build()
        );

        UserRequestDTO dto = UserRequestDTO.builder()
                .password("password")
                .newPassword("changedpw")
                .build();

        // when & then
        assertThatThrownBy(() ->
                userService.updateUserPassword("social@test.com", dto)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_REQUEST);
    }
    @Test
    @DisplayName("비밀번호 변경 실패 - 기존 비밀번호 불일치")
    void updatePassword_wrongPassword_fail() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("pw@test.com")
                        .password("password")
                        .nickname("닉네임")
                        .build()
        );

        UserRequestDTO dto = UserRequestDTO.builder()
                .password("wrongpw")        // 틀린 기존 비밀번호
                .newPassword("changedpw")
                .build();

        // when & then
        assertThatThrownBy(() ->
                userService.updateUserPassword("pw@test.com", dto)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PASSWORD);
    }
    @Test
    @DisplayName("비밀번호 변경 실패 - 기존 비밀번호와 동일")
    void updatePassword_samePassword_fail() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("pw@test.com")
                        .password("password")
                        .nickname("닉네임")
                        .build()
        );

        UserRequestDTO dto = UserRequestDTO.builder()
                .password("password")
                .newPassword("password")   // 동일
                .build();

        // when & then
        assertThatThrownBy(() ->
                userService.updateUserPassword("pw@test.com", dto)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SAME_AS_OLD_PASSWORD);
    }
    @Test
    @DisplayName("회원 탈퇴 성공 - LOCAL 회원")
    void deleteUser_local_success() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("delete@test.com")
                        .password("password")
                        .nickname("탈퇴유저")
                        .build()
        );

        UserRequestDTO deleteDto = UserRequestDTO.builder()
                .password("password")
                .build();

        // when
        userService.deleteUser("delete@test.com", deleteDto);

        // then
        UserEntity deletedUser = userRepository
                .findByEmailAndDeleted("delete@test.com", true)
                .orElseThrow();

        assertThat(deletedUser.isDeleted()).isTrue();
    }
    @Test
    @DisplayName("회원 탈퇴 실패 - LOCAL 회원 비밀번호 불일치")
    void deleteUser_local_wrongPassword_fail() {
        // given
        userService.createUser(
                UserRequestDTO.builder()
                        .email("delete@test.com")
                        .password("password")
                        .nickname("탈퇴유저")
                        .build()
        );

        UserRequestDTO deleteDto = UserRequestDTO.builder()
                .password("wrongpw")
                .build();

        // when & then
        assertThatThrownBy(() ->
                userService.deleteUser("delete@test.com", deleteDto)
        )
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_PASSWORD);
    }


}
