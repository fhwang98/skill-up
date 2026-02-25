package com.skillup.backend.domain.study.dto;

import com.skillup.backend.domain.study.entity.StudyCategory;
import com.skillup.backend.domain.study.entity.StudyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class StudyDetailResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String leaderNickname;
    private int maxMembers;
    private int currentMembers;
    private StudyStatus status;
    private StudyCategory category;
    private List<String> tags;
    private long viewCount;
    private LocalDate recruitEndDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
}
