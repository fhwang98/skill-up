package com.skillup.backend.domain.study.service;

import com.skillup.backend.domain.study.dto.StudyRequestDTO;
import com.skillup.backend.domain.study.dto.StudyResponseDTO;
import com.skillup.backend.domain.study.entity.StudyEntity;
import com.skillup.backend.domain.study.repository.StudyRepository;
import com.skillup.backend.domain.tag.entity.TagEntity;
import com.skillup.backend.domain.tag.repository.TagRepository;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {

    private final StudyRepository studyRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createStudy(String email, StudyRequestDTO dto) {

        log.info("스터디 생성 요청 email: {}", email);
        UserEntity leader = userRepository.findByEmailAndDeleted(email, false)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 유저");
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        StudyEntity study = StudyEntity.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .leader(leader)
                .maxMembers(dto.getMaxMembers())
                .category(dto.getCategory())
                .recruitEndDate(dto.getRecruitEndDate())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();

        // 태그 처리
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {

            // 정규화 + 중복 제거
            Set<String> normalizedTags = dto.getTags().stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .filter(tag -> !tag.isBlank())
                    .collect(Collectors.toSet());

            List<TagEntity> tags = tagRepository.findByNameIn(normalizedTags);

            if (tags.size() != normalizedTags.size()) {
                log.warn("존재하지 않는 태그");
                throw new CustomException(ErrorCode.INVALID_TAG);
            }

            tags.forEach(study::addTag);
        }

        return studyRepository.save(study).getId();
    }

    @Transactional(readOnly = true)
    public Page<StudyResponseDTO> getStudies(String keyword, Pageable pageable) {
        Page<StudyEntity> page;
        if (keyword == null || keyword.isBlank()) {
            page = studyRepository.findAll(pageable);
        } else {
            page = studyRepository.findByTitleContaining(keyword, pageable);
        }
       return page.map(study -> StudyResponseDTO.builder()
                            .id(study.getId())
                            .title(study.getTitle())
                            .status(study.getStatus())
                            .category(study.getCategory())
                            .createdAt(study.getCreatedAt())
                            .nickname(study.getLeader().getNickname())
                            .tags(study.getStudyTags().stream()
                                    .map(st -> st.getTag().getName())
                                    .toList())
                            .build());
    }

}

