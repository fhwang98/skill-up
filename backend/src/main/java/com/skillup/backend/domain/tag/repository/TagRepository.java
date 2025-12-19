package com.skillup.backend.domain.tag.repository;

import com.skillup.backend.domain.tag.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

    List<TagEntity> findByNameIn(Collection<String> names);
    List<TagEntity> findByNameContainingIgnoreCaseOrderByName(String keyword);

}
