package com.skillup.backend.domain.study.entity;

import com.skillup.backend.domain.tag.entity.TagEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "tbl_study_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = {"study_id", "tag_id"})
)
public class StudyTagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id", nullable = false)
    private StudyEntity study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private TagEntity tag;
}
