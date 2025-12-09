package com.skillup.backend.domain.user.api;

import com.skillup.backend.domain.user.dto.UserRequestDTO;
import com.skillup.backend.domain.user.service.UserService;
import com.skillup.backend.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // 자체 회원가입 api
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<Map<String, Long>>> createUser(@Validated @RequestBody UserRequestDTO dto) {

        log.info("회원가입 api 요청 POST /users");
        Long id = userService.createUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userId", id);
        return ResponseEntity.status(201).body(ApiResponse.success(responseBody));

    }

}
