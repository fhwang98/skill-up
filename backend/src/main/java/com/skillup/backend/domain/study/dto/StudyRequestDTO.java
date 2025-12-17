package com.skillup.backend.domain.study.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Max(10)
    private int maxMembers;

    @Size(max = 10)
    private List<@NotBlank String> tags;
}
