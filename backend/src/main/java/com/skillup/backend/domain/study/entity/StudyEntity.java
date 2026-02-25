package com.skillup.backend.domain.study.entity;

import com.skillup.backend.domain.tag.entity.TagEntity;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbl_study")
public class StudyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 스터디 제목
    @Column(nullable = false)
    private String title;

    // 스터디 설명
    @Column(nullable = false, length = 1000)
    private String description;

    // 스터디장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private UserEntity leader;

    // 최대 인원
    @Column(nullable = false)
    private int maxMembers;

    // 현재 인원
    @Column(nullable = false)
    @Builder.Default
    private int currentMembers = 1;

    // 모집 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StudyStatus status = StudyStatus.OPENED; // OPENED, CLOSED

    @OneToMany(
            mappedBy = "study",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<StudyTagEntity> studyTags = new ArrayList<>();

    // 모집 마감일
    @Column(nullable = false)
    private LocalDate recruitEndDate;

    // 스터디 시작일
    @Column(nullable = false)
    private LocalDate startDate;

    // 스터디 종료일 (선택)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyCategory category;

    @Column(nullable = false)
    @Builder.Default
    private long viewCount = 0;

    @Builder.Default
    private boolean deleted = false;

    public void addTag(TagEntity tag) {
        this.studyTags.add(
                StudyTagEntity.builder()
                        .study(this)
                        .tag(tag)
                        .build()
        );
    }

    public void update(String title, String description, int maxMembers,
                       StudyStatus status, java.time.LocalDate recruitEndDate,
                       java.time.LocalDate startDate, java.time.LocalDate endDate) {
        this.title = title;
        this.description = description;
        this.maxMembers = maxMembers;
        this.status = status;
        this.recruitEndDate = recruitEndDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void clearTags() {
        this.studyTags.clear();
    }

    public void delete() {
        this.deleted = true;
    }

}
