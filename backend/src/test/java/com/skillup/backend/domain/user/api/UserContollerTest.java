package com.skillup.backend.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.domain.user.dto.UserRequestDTO;
import com.skillup.backend.domain.user.dto.UserResponseDTO;
import com.skillup.backend.domain.user.service.UserService;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공 시 201 Created 반환 + userId 반환")
    void signup_success() throws Exception {

        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("test@test.com")
                .password("password")
                .nickname("testuser")
                .build();

        Mockito.when(userService.createUser(any())).thenReturn(1L);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1L));
    }

    @Test
    @DisplayName("중복 이메일 - 회원가입 실패 시 409 반환")
    void signup_duplicateEmail_fail() throws Exception {

        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("dup@test.com")
                .password("password")
                .nickname("duper")
                .build();

        Mockito.doThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL))
                .when(userService).createUser(any());

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_EMAIL"));
    }

    @Test
    @DisplayName("유효성 검증 실패 - 400 Bad Request 반환")
    void signup_invalidRequest_fail() throws Exception {

        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("")
                .password("")
                .nickname("")
                .build();

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("이메일 중복 검사 성공 - 200")
    void existEmail_ReturnsTrue_WhenEmailExists() throws Exception {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("dup@test.com")
                .build();

        given(userService.existsByEmail(any(UserRequestDTO.class)))
                .willReturn(true);

        // when & then
        mockMvc.perform(post("/users/exist-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("이메일 중복 검사 이메일 미입력 시 Validation 실패 - 400")
    void existEmail_ReturnsBadRequest_WhenEmailIsBlank() throws Exception {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .email("")
                .build();

        // when & then
        mockMvc.perform(post("/users/exist-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 검사 성공 - 200")
    void existNickname_ReturnsTrue_WhenNicknameExists() throws Exception {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .nickname("dupNickname")
                .build();

        given(userService.existsByNickname(any(UserRequestDTO.class)))
                .willReturn(true);

        // when & then
        mockMvc.perform(post("/users/exist-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.exists").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("닉네임 중복 검사 닉네임 미입력 시 Validation 실패 - 400")
    void existNickname_ReturnsBadRequest_WhenNicknameIsBlank() throws Exception {
        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .nickname("")
                .build();

        // when & then
        mockMvc.perform(post("/users/exist-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 조회 성공 - 로그인 사용자")
    void getUserMe_success() throws Exception {

        // given
        LocalDateTime now = LocalDateTime.now();
        UserResponseDTO response =
                UserResponseDTO.builder()
                        .email("me@test.com")
                        .nickname("내닉네임")
                        .createdAt(now.minusDays(1))
                        .updatedAt(now)
                        .build();

        Mockito.when(userService.getByEmail("me@test.com"))
                .thenReturn(response);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("me@test.com");

        // when & then
        mockMvc.perform(get("/users/me")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("me@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("내닉네임"))
                .andExpect(jsonPath("$.data.createdAt").value(now.minusDays(1).toString()))
                .andExpect(jsonPath("$.data.updatedAt").value(now.toString()));
    }

    @Test
    @DisplayName("내 정보 수정 성공 - 닉네임 변경")
    void updateUserMe_success() throws Exception {

        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .nickname("변경닉네임")
                .build();

        Mockito.when(userService.updateUser(Mockito.eq("me@test.com"), any()))
                .thenReturn(1L);

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("me@test.com");

        // when & then
        mockMvc.perform(patch("/users/me")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1L));
    }

    @Test
    @DisplayName("내 정보 수정 실패 - Validation 오류")
    void updateUserMe_validation_fail() throws Exception {

        // given
        UserRequestDTO dto = UserRequestDTO.builder()
                .nickname("")
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("me@test.com");

        // when & then
        mockMvc.perform(patch("/users/me")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
