package com.skillup.backend.domain.study.repository;

import com.skillup.backend.domain.study.entity.StudyCategory;
import com.skillup.backend.domain.study.entity.StudyEntity;
import com.skillup.backend.domain.study.entity.StudyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudyRepository extends JpaRepository<StudyEntity, Long> {

    // 상세 조회 — leader, studyTags, studyTags.tag 한 번에 페치
    @EntityGraph(attributePaths = {"leader", "studyTags", "studyTags.tag"})
    Optional<StudyEntity> findWithDetailsById(Long id);

    // 목록 조회 — N+1 방지 + 동적 필터 (keyword, category, status)
    @EntityGraph(attributePaths = {"leader", "studyTags", "studyTags.tag"})
    @Query("""
            SELECT s FROM StudyEntity s
            WHERE s.deleted = false
              AND (:keyword  IS NULL OR s.title LIKE %:keyword%)
              AND (:category IS NULL OR s.category = :category)
              AND (:status   IS NULL OR s.status   = :status)
            """)
    Page<StudyEntity> findAllWithFilter(
            @Param("keyword")  String keyword,
            @Param("category") StudyCategory category,
            @Param("status")   StudyStatus status,
            Pageable pageable
    );
}
