package com.skillup.backend.domain.tag.entity;

import com.skillup.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "tbl_tag",
        uniqueConstraints = @UniqueConstraint(columnNames = "name")
)
public class TagEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 태그명 (java, spring, jpa)
    @Column(nullable = false, length = 30)
    private String name;
}
