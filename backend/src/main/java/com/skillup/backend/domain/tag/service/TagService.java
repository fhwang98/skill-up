package com.skillup.backend.domain.tag.service;

import com.skillup.backend.domain.tag.entity.TagEntity;
import com.skillup.backend.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<String> search(String keyword) {
        return tagRepository.findByNameContainingIgnoreCaseOrderByName(keyword)
                .stream()
                .map(TagEntity::getName)
                .toList();
    }

}
