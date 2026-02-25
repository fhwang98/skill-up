package com.skillup.backend.domain.study.dto;

import com.skillup.backend.domain.study.entity.StudyCategory;
import com.skillup.backend.domain.study.entity.StudyStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyUpdateRequestDTO {

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
    private StudyStatus status;

    @NotNull
    private LocalDate recruitEndDate;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;
}
