package com.skillup.backend.domain.study.api;

import com.skillup.backend.domain.study.dto.StudyDetailResponseDTO;
import com.skillup.backend.domain.study.dto.StudyRequestDTO;
import com.skillup.backend.domain.study.dto.StudyResponseDTO;
import com.skillup.backend.domain.study.dto.StudyUpdateRequestDTO;
import com.skillup.backend.domain.study.entity.StudyCategory;
import com.skillup.backend.domain.study.entity.StudyStatus;
import com.skillup.backend.domain.study.service.StudyService;
import com.skillup.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/studies")
@Tag(name = "STUDY API", description = "스터디 API")
public class StudyController {

    private final StudyService studyService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "스터디 생성", description = "스터디 생성 api")
    public ResponseEntity<BaseResponse<Map<String, Long>>> createStudy(
            Authentication authentication,
            @Valid @RequestBody StudyRequestDTO dto
    ) {
        String email = authentication.getName();
        log.info("스터디 생성 요청 POST /studies - email:{}", email);
        Long id = studyService.createStudy(email, dto);
        return ResponseEntity.ok(BaseResponse.success(Collections.singletonMap("studyId", id)));
    }

    @GetMapping
    @Operation(summary = "스터디 목록 조회", description = "키워드 검색 + 카테고리/상태 필터 + 페이징")
    public ResponseEntity<BaseResponse<Page<StudyResponseDTO>>> getStudies(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) StudyCategory category,
            @RequestParam(required = false) StudyStatus status,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("스터디 목록 요청 GET /studies - keyword:{}, category:{}, status:{}", keyword, category, status);
        Page<StudyResponseDTO> page = studyService.getStudies(keyword, category, status, pageable);
        return ResponseEntity.ok(BaseResponse.success(page));
    }

    @GetMapping("/{id}")
    @Operation(summary = "스터디 상세 조회", description = "스터디 단건 상세 조회 api")
    public ResponseEntity<BaseResponse<StudyDetailResponseDTO>> getStudyDetail(
            @PathVariable Long id
    ) {
        log.info("스터디 상세 조회 요청 GET /studies/{}", id);
        StudyDetailResponseDTO dto = studyService.getStudyDetail(id);
        return ResponseEntity.ok(BaseResponse.success(dto));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "스터디 수정", description = "스터디장만 가능. 태그는 전체 교체 방식.")
    public ResponseEntity<BaseResponse<Map<String, Long>>> updateStudy(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody StudyUpdateRequestDTO dto
    ) {
        String email = authentication.getName();
        log.info("스터디 수정 요청 PATCH /studies/{} - email:{}", id, email);
        Long studyId = studyService.updateStudy(email, id, dto);
        return ResponseEntity.ok(BaseResponse.success(Collections.singletonMap("studyId", studyId)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "스터디 삭제", description = "스터디장만 가능. Soft Delete.")
    public ResponseEntity<BaseResponse<Void>> deleteStudy(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String email = authentication.getName();
        log.info("스터디 삭제 요청 DELETE /studies/{} - email:{}", id, email);
        studyService.deleteStudy(email, id);
        return ResponseEntity.ok(BaseResponse.success(null));
    }
}

