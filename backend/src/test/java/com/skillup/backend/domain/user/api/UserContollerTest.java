package com.skillup.backend.domain.user.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillup.backend.domain.user.dto.UserRequestDTO;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        UserRequestDTO dto = new UserRequestDTO("test@test.com", "password", "testuser");
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

        UserRequestDTO dto = new UserRequestDTO("dup@test.com", "password", "duper");
        Mockito.doThrow(new CustomException(ErrorCode.DUPLICATE_EMAIL))
                .when(userService).createUser(any());

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

        UserRequestDTO dto = new UserRequestDTO("", "", ""); // 모든 필드 유효성 실패

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
