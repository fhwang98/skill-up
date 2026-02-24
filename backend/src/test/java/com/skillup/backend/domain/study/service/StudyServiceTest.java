package com.skillup.backend.domain.study.service;

import com.skillup.backend.domain.study.dto.StudyDetailResponseDTO;
import com.skillup.backend.domain.study.dto.StudyRequestDTO;
import com.skillup.backend.domain.study.dto.StudyResponseDTO;
import com.skillup.backend.domain.study.entity.StudyCategory;
import com.skillup.backend.domain.study.entity.StudyStatus;
import com.skillup.backend.domain.tag.entity.TagEntity;
import com.skillup.backend.domain.tag.repository.TagRepository;
import com.skillup.backend.domain.user.entity.SocialProviderType;
import com.skillup.backend.domain.user.entity.UserEntity;
import com.skillup.backend.domain.user.entity.UserRoleType;
import com.skillup.backend.domain.user.repository.UserRepository;
import com.skillup.backend.global.exception.CustomException;
import com.skillup.backend.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class StudyServiceTest {

    @Autowired
    private StudyService studyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    private UserEntity leader;

    @BeforeEach
    void setUp() {
        // 각 테스트마다 사용할 리더 유저 생성
        leader = userRepository.save(UserEntity.builder()
                .email("leader@test.com")
                .password("password")
                .nickname("리더")
                .role(UserRoleType.ROLE_USER)
                .provider(SocialProviderType.LOCAL)
                .deleted(false)
                .build());

        // 태그 데이터 준비
        tagRepository.save(TagEntity.builder().name("java").build());
        tagRepository.save(TagEntity.builder().name("spring").build());
    }

    private StudyRequestDTO buildRequest(String title, List<String> tags) {
        return StudyRequestDTO.builder()
                .title(title)
                .description("스터디 설명입니다.")
                .maxMembers(5)
                .tags(tags)
                .category(StudyCategory.BACKEND)
                .recruitEndDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(14))
                .endDate(LocalDate.now().plusDays(60))
                .build();
    }

    // ─────────────────────────────────────────
    // 스터디 생성
    // ─────────────────────────────────────────

    @Test
    @DisplayName("스터디 생성 성공")
    void createStudy_success() {
        // given
        StudyRequestDTO dto = buildRequest("자바 스터디", List.of("java", "spring"));

        // when
        Long studyId = studyService.createStudy(leader.getEmail(), dto);

        // then
        assertThat(studyId).isNotNull();

        StudyDetailResponseDTO saved = studyService.getStudyDetail(studyId);
        assertThat(saved.getTitle()).isEqualTo("자바 스터디");
        assertThat(saved.getLeaderNickname()).isEqualTo("리더");
        assertThat(saved.getMaxMembers()).isEqualTo(5);
        assertThat(saved.getCurrentMembers()).isEqualTo(1);
        assertThat(saved.getStatus()).isEqualTo(StudyStatus.OPENED);
        assertThat(saved.getTags()).containsExactlyInAnyOrder("java", "spring");
    }

    @Test
    @DisplayName("스터디 생성 성공 - 태그 없이도 생성 가능")
    void createStudy_withoutTags_success() {
        // given
        StudyRequestDTO dto = buildRequest("태그없는 스터디", null);

        // when
        Long studyId = studyService.createStudy(leader.getEmail(), dto);

        // then
        StudyDetailResponseDTO saved = studyService.getStudyDetail(studyId);
        assertThat(saved.getTags()).isEmpty();
    }

    @Test
    @DisplayName("스터디 생성 실패 - 존재하지 않는 유저")
    void createStudy_userNotFound_fail() {
        // given
        StudyRequestDTO dto = buildRequest("스터디", List.of("java"));

        // when & then
        assertThatThrownBy(() -> studyService.createStudy("nobody@test.com", dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("스터디 생성 실패 - 존재하지 않는 태그")
    void createStudy_invalidTag_fail() {
        // given
        StudyRequestDTO dto = buildRequest("스터디", List.of("없는태그"));

        // when & then
        assertThatThrownBy(() -> studyService.createStudy(leader.getEmail(), dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TAG);
    }

    @Test
    @DisplayName("스터디 생성 실패 - 태그 대소문자 정규화 후 존재하지 않으면 실패")
    void createStudy_tagCaseNormalized_fail() {
        // given — "JAVA"는 정규화 후 "java"가 되지만 DB에 없는 "없는태그"는 실패
        StudyRequestDTO dto = buildRequest("스터디", List.of("JAVA", "없는태그"));

        // when & then
        assertThatThrownBy(() -> studyService.createStudy(leader.getEmail(), dto))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_TAG);
    }

    // ─────────────────────────────────────────
    // 스터디 목록 조회
    // ─────────────────────────────────────────

    @Test
    @DisplayName("스터디 목록 조회 성공 - 전체 조회")
    void getStudies_all_success() {
        // given
        studyService.createStudy(leader.getEmail(), buildRequest("자바 스터디", List.of("java")));
        studyService.createStudy(leader.getEmail(), buildRequest("스프링 스터디", List.of("spring")));

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<StudyResponseDTO> result = studyService.getStudies(null, null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("스터디 목록 조회 성공 - 키워드 필터")
    void getStudies_keywordFilter_success() {
        // given
        studyService.createStudy(leader.getEmail(), buildRequest("자바 스터디", List.of("java")));
        studyService.createStudy(leader.getEmail(), buildRequest("스프링 스터디", List.of("spring")));

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<StudyResponseDTO> result = studyService.getStudies("자바", null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("자바 스터디");
    }

    @Test
    @DisplayName("스터디 목록 조회 성공 - 카테고리 필터")
    void getStudies_categoryFilter_success() {
        // given
        studyService.createStudy(leader.getEmail(), buildRequest("백엔드 스터디", List.of("java")));

        // 프론트엔드 카테고리 스터디 생성 (직접 저장)
        StudyRequestDTO frontDto = StudyRequestDTO.builder()
                .title("프론트 스터디")
                .description("설명")
                .maxMembers(4)
                .category(StudyCategory.FRONTEND)
                .recruitEndDate(LocalDate.now().plusDays(7))
                .startDate(LocalDate.now().plusDays(14))
                .build();
        studyService.createStudy(leader.getEmail(), frontDto);

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        // when
        Page<StudyResponseDTO> result = studyService.getStudies(null, StudyCategory.BACKEND, null, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getCategory()).isEqualTo(StudyCategory.BACKEND);
    }

    // ─────────────────────────────────────────
    // 스터디 상세 조회
    // ─────────────────────────────────────────

    @Test
    @DisplayName("스터디 상세 조회 성공")
    void getStudyDetail_success() {
        // given
        Long studyId = studyService.createStudy(leader.getEmail(), buildRequest("자바 스터디", List.of("java", "spring")));

        // when
        StudyDetailResponseDTO detail = studyService.getStudyDetail(studyId);

        // then
        assertThat(detail.getId()).isEqualTo(studyId);
        assertThat(detail.getTitle()).isEqualTo("자바 스터디");
        assertThat(detail.getDescription()).isEqualTo("스터디 설명입니다.");
        assertThat(detail.getLeaderNickname()).isEqualTo("리더");
        assertThat(detail.getMaxMembers()).isEqualTo(5);
        assertThat(detail.getViewCount()).isEqualTo(0);
        assertThat(detail.getCategory()).isEqualTo(StudyCategory.BACKEND);
        assertThat(detail.getTags()).containsExactlyInAnyOrder("java", "spring");
        assertThat(detail.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("스터디 상세 조회 실패 - 존재하지 않는 스터디")
    void getStudyDetail_notFound_fail() {
        // when & then
        assertThatThrownBy(() -> studyService.getStudyDetail(999L))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STUDY_NOT_FOUND);
    }
}
