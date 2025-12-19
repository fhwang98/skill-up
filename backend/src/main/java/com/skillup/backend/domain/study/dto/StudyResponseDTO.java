package com.skillup.backend.domain.study.dto;

import com.skillup.backend.domain.study.entity.StudyCategory;
import com.skillup.backend.domain.study.entity.StudyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StudyResponseDTO {
    private Long id;
    private String title;
    private String nickname;
    private StudyStatus status;
    private StudyCategory category;
    private List<String> tags;
    private LocalDateTime createdAt;
}