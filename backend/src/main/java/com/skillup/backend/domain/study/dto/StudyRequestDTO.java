package com.skillup.backend.domain.study.dto;

import com.skillup.backend.domain.study.entity.StudyCategory;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRequestDTO {

    @NotBlank
    private String title;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @Min(2)
    private int maxMembers;

    @Size(max = 10)
    private List<@NotBlank String> tags;

    @NotNull
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @NotNull
    private LocalDateTime recruitEndDate;

    @NotNull
    private StudyCategory category;

}
