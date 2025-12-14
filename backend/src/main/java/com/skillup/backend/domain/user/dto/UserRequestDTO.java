package com.skillup.backend.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserRequestDTO {

    public interface existEmailGroup {} // 회원 가입시 이메일 중복 검사
    public interface existNicknameGroup {} // 회원 가입시 이메일 중복 검사
    public interface createGroup {} // 회원 가입시
    public interface updateGroup {}
    public interface passwordGroup {}

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일을 입력해주세요.", groups = {createGroup.class, existEmailGroup.class})
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.", groups = {createGroup.class, passwordGroup.class})
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]{8,16}$", message = "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자로 이루어져야 합니다.")
    private String password;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.", groups = {passwordGroup.class})
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]{8,16}$", message = "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자로 이루어져야 합니다.")
    private String newPassword;

    @NotBlank(message = "닉네임을 입력해주세요.", groups = {createGroup.class, existNicknameGroup.class, updateGroup.class})
    @Size(min = 2, max = 10, message = "닉네임은 2~10자로 입력해주세요.")
    private String nickname;
}
