package com.skillup.backend.domain.study.entity;

import com.skillup.backend.domain.tag.entity.TagEntity;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    // 최대 인원
    @Column(nullable = false)
    private int maxMembers;

    // 현재 인원
    @Column(nullable = false)
    private int currentMembers;

    // 모집 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StudyStatus status;

    @OneToMany(
            mappedBy = "study",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<StudyTagEntity> studyTags = new ArrayList<>();

    public void addTag(TagEntity tag) {
        this.studyTags.add(
                StudyTagEntity.builder()
                        .study(this)
                        .tag(tag)
                        .build()
        );
    }

}
