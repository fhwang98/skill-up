package com.skillup.backend.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponseDTO {
    private String email;
    private String nickname;
    private String provider;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
