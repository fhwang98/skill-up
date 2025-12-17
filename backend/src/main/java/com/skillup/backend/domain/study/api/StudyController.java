package com.skillup.backend.domain.study.api;

import com.skillup.backend.domain.study.dto.StudyRequestDTO;
import com.skillup.backend.domain.study.service.StudyService;
import com.skillup.backend.global.common.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        Map<String, Long> responseBody = Collections.singletonMap("studyId", id);
        return ResponseEntity.ok(BaseResponse.success(responseBody));
    }
}
