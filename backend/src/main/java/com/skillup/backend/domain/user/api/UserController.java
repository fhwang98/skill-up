package com.skillup.backend.domain.user.api;

import com.skillup.backend.domain.user.dto.UserRequestDTO;
import com.skillup.backend.domain.user.dto.UserResponseDTO;
import com.skillup.backend.domain.user.service.UserService;
import com.skillup.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "USER API", description = "회원 API")
public class UserController {

    private final UserService userService;

    // 이메일 중복체크 api
    @Operation(summary = "이메일 중복 체크", description = "회원가입 시 이메일 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "확인 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검증 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping(value = "/exist-email")
    public ResponseEntity<BaseResponse<Map<String, Boolean>>> existEmail(@Validated(UserRequestDTO.existEmailGroup.class) @RequestBody UserRequestDTO dto) {
        log.info("이메일 중복검사 요청 POST /users/exist-email: {}", dto.getEmail());
        Boolean exists = userService.existsByEmail(dto);
        Map<String, Boolean> responseBody = Collections.singletonMap("exists", exists);
        return ResponseEntity.ok(BaseResponse.success(responseBody));
    }
    // 닉네임 중복체크 api
    @Operation(summary = "닉네임 중복 체크", description = "회원가입 시 닉네임 중복 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "확인 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검증 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping(value = "/exist-nickname")
    public ResponseEntity<BaseResponse<Map<String, Boolean>>> existNickname(@Validated(UserRequestDTO.existNicknameGroup.class) @RequestBody UserRequestDTO dto) {
        log.info("닉네임 중복검사 요청 POST /users/exist-nickname: {}", dto.getNickname());
        Boolean exists = userService.existsByNickname(dto);
        Map<String, Boolean> responseBody = Collections.singletonMap("exists", exists);
        return ResponseEntity.ok(BaseResponse.success(responseBody));
    }

    // 자체 회원가입 api
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "자체 회원가입", description = "회원가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효성 검증 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "409", description = "이메일 중복", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    public ResponseEntity<BaseResponse<Map<String, Long>>> createUser(@Validated(UserRequestDTO.createGroup.class) @RequestBody UserRequestDTO dto) {

        log.info("회원가입 api 요청 POST /users");
        Long id = userService.createUser(dto);
        Map<String, Long> responseBody = Collections.singletonMap("userId", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(responseBody));

    }


    @GetMapping(value = "/me")
    @Operation(summary = "유저 정보 확인", description = "로그인한 유저의 이메일과 닉네임을 확인합니다.")
    public ResponseEntity<BaseResponse<UserResponseDTO>> getUser(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(BaseResponse.success(userService.getByEmail(email)));
    }

}
