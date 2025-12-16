package com.skillup.backend.domain.study.repository;

import com.skillup.backend.domain.study.entity.StudyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<StudyEntity, Long> {
}
