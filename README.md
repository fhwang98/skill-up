# SkillUp

스터디/모임을 생성하고, 참가 신청과 관리를 할 수 있는 스터디 매칭 플랫폼입니다.
Spring Security, JWT, OAuth2, JPA 기반 백엔드 역량을 증명하기 위한 포트폴리오 프로젝트입니다.

---

## 1. 프로젝트 개요

- 프로젝트명: SkillUp
- 개발 목적: 스터디 매칭 서비스 구현을 통한 백엔드 핵심 역량 증명
- 개발 기간: 2025.12 ~ 진행중
- GitHub: https://github.com/fhwang98/skill-up

---

## 2. 구현 목표

- Spring Security + JWT 인증 및 인가 흐름 이해
- OAuth2 소셜 로그인(Naver/Kakao)
- RESTful API 설계 및 Swagger 문서화
- Spring Data JPA 기반 CRUD/검색/페이징 처리
- Logback 기반 요청/응답/예외 로깅
- JUnit + AssertJ 기반 유닛 테스트 작성

---

## 3. 기술 스택

### Backend

| 항목      | 사용 기술                                         |
| --------- | ------------------------------------------------- |
| Language  | Java 17                                           |
| Framework | Spring Boot 3.5.8                                 |
| Build     | Gradle                                            |
| Security  | Spring Security, OAuth2 Client, JWT (jjwt 0.12.6) |
| DB        | H2 (개발/테스트) → MySQL 8 (운영 전환 예정)       |
| ORM       | Spring Data JPA                                   |
| Cache     | Redis (Spring Data Redis)                         |
| API Docs  | Springdoc OpenAPI 2.8.14 (Swagger UI)             |
| Test      | JUnit5 + AssertJ + Spring Security Test           |
| Logging   | Logback                                           |

### Frontend

| 항목     | 사용 기술  |
| -------- | ---------- |
| Language | JavaScript |
| Library  | React 18   |
| HTTP     | fetch API  |
| UI       | shadcn/ui  |
| Build    | Vite       |

---

## 4. 주요 기능

### 인증 / 회원 ✅ 구현 완료

- 회원가입 / 로그인 / 로그아웃
- JWT 기반 Access / Refresh Token 인증
- Redis 기반 RefreshToken 관리
- OAuth2 소셜 로그인 (Kakao, Naver)
- 소셜 로그인 AccessToken 관리 (SocialToken)
- 내 정보 조회 / 수정
- 비밀번호 변경
- 회원 탈퇴 (Soft Delete)

### 스터디 ✅ 일부 구현 완료

- 스터디 생성
- 스터디 목록 조회 (키워드 검색 + 카테고리/상태 필터 + 페이징)
- 스터디 상세 조회
- 카테고리: `BACKEND`, `FRONTEND`, `ALGORITHM`, `CS`, `PROJECT`
- 상태: `OPENED`, `CLOSED`

### 태그 ✅ 구현 완료

- 태그 키워드 검색 (`GET /tags?keyword=`)

### 미구현 (예정)

- 스터디 수정 / 삭제
- 스터디 참가 신청 / 취소 / 승인 / 거절
- 스터디 북마크 등록 / 해제

---

## 5. 시스템 아키텍처

```
[React]  <---- HTTP(JSON, JWT) ---->  [Spring Boot API]  <---->  [H2 / MySQL]
  |                                         |
  |---- OAuth2 Redirect Flow(Provider) -----|              [Redis (RefreshToken)]
```

- 모든 기능은 REST API 호출 기반
- 인증 요청은 `Authorization: Bearer <AccessToken>` 방식
- 현재 DB는 H2(인메모리) 사용, 운영 전환 시 MySQL 8로 교체 예정

---

## 6. 패키지 구조

```
com.skillup.backend
 ├─ BackendApplication.java
 ├─ global
 │   ├─ common          # BaseEntity, BaseResponse, ErrorResponse
 │   ├─ config          # Security, JPA Auditing, Redis, OpenAPI
 │   ├─ exception       # CustomException, ErrorCode, GlobalExceptionHandler
 │   ├─ filter          # JWTFilter, LoginFilter
 │   ├─ handler         # 인증/인가 핸들러, 로그인/로그아웃 핸들러
 │   └─ util            # JWTUtil
 └─ domain
     ├─ user
     │   ├─ api         # UserController
     │   ├─ dto
     │   ├─ entity      # UserEntity, UserRoleType, SocialProviderType
     │   ├─ repository
     │   └─ service
     ├─ auth
     │   ├─ api         # AuthController
     │   ├─ dto         # CustomOAuth2User, CustomUserDetails
     │   ├─ entity      # RefreshTokenEntity, SocialTokenEntity
     │   ├─ oauth       # OAuth2 Client / Service
     │   ├─ repository
     │   └─ service     # AuthService, RefreshTokenService, SocialTokenService
     ├─ study
     │   ├─ api         # StudyController
     │   ├─ dto
     │   ├─ entity      # StudyEntity, StudyTagEntity, StudyCategory, StudyStatus
     │   ├─ repository
     │   └─ service
     └─ tag
         ├─ api         # TagController
         ├─ entity      # TagEntity
         ├─ repository
         └─ service
```

---

## 7. ERD 개요

| Entity       | 설명                          |
| ------------ | ----------------------------- |
| User         | 회원                          |
| Study        | 스터디                        |
| StudyTag     | Study-Tag N:M 중간 엔티티     |
| Tag          | 태그                          |
| RefreshToken | JWT RefreshToken 관리 (Redis) |
| SocialToken  | OAuth2 소셜 AccessToken 관리  |

관계 요약

- User 1 : N Study (leader)
- Study N : M Tag (StudyTag)
- User 1 : N RefreshToken
- User 1 : N SocialToken

> StudyMember(참가 정보), StudyBookmark는 미구현 상태입니다.

---

## 8. REST API 요약

context-path: `/api/v1`

### Auth

| Method | URL              | 인증 필요 | 기능        |
| ------ | ---------------- | --------- | ----------- |
| POST   | `/auth/login`    | ❌        | 로그인      |
| POST   | `/auth/refresh`  | ❌        | 토큰 재발급 |
| POST   | `/auth/logout`   | ✅        | 로그아웃    |

### User

| Method | URL                      | 인증 필요 | 기능             |
| ------ | ------------------------ | --------- | ---------------- |
| POST   | `/users`                 | ❌        | 회원가입         |
| POST   | `/users/exist-email`     | ❌        | 이메일 중복 확인 |
| POST   | `/users/exist-nickname`  | ❌        | 닉네임 중복 확인 |
| GET    | `/users/me`              | ✅        | 내 정보 조회     |
| PATCH  | `/users/me`              | ✅        | 닉네임 수정      |
| PATCH  | `/users/me/password`     | ✅        | 비밀번호 변경    |
| DELETE | `/users/me`              | ✅        | 회원 탈퇴        |

### Study

| Method | URL              | 인증 필요 | 기능                                           |
| ------ | ---------------- | --------- | ---------------------------------------------- |
| POST   | `/studies`       | ✅        | 스터디 생성                                    |
| GET    | `/studies`       | ❌        | 스터디 목록 조회 (키워드/카테고리/상태/페이징) |
| GET    | `/studies/{id}`  | ❌        | 스터디 상세 조회                               |
| PATCH  | `/studies/{id}`  | ✅        | 스터디 수정 (미구현)                           |
| DELETE | `/studies/{id}`  | ✅        | 스터디 삭제 (미구현)                           |

### Tag

| Method | URL      | 인증 필요 | 기능                           |
| ------ | -------- | --------- | ------------------------------ |
| GET    | `/tags`  | ❌        | 태그 키워드 검색 (`?keyword=`) |

---

## 9. 실행 방법

### Backend

```bash
# 빌드
./gradlew clean build

# 실행
java -jar build/libs/skillup-0.0.1-SNAPSHOT.jar
```

Swagger UI: `http://localhost:8080/api/v1/swagger-ui/index.html`

> 현재 H2 인메모리 DB로 실행됩니다. MySQL 전환 시 `.env` 파일에 DB 접속 정보를 설정하고
> `build.gradle`의 H2 의존성을 MySQL Connector(`com.mysql:mysql-connector-j`)로 교체하세요.

### Frontend

```bash
npm install
npm run dev
```

환경 변수 예시 (`.env`):

```
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

---

## 10. 테스트

JUnit5 + AssertJ + Spring Security Test 기반으로 단위 테스트를 작성하고 있습니다.

현재 작성된 테스트:

- `AuthServiceTest` - 로그인, 토큰 발급 관련 서비스 테스트
- `AuthServiceOAuth2Test` - OAuth2 소셜 로그인 서비스 테스트
- `RefreshTokenServiceTest` - RefreshToken 갱신/검증 테스트
- `UserServiceTest` - 회원가입, 정보 수정 등 서비스 테스트
- `SocialUserTest` - 소셜 회원 관련 서비스 테스트
- `UserControllerTest` - UserController Mock 기반 통합 테스트
- `StudyServiceTest` - 스터디 생성/조회 서비스 테스트

---

## 11. 향후 개선 사항

- 스터디 수정 / 삭제 API 구현
- 스터디 참가 신청 / 승인 / 거절 기능
- 스터디 북마크 기능
- 알림 기능
- 스터디 내 게시판 / 댓글 기능
- 신고 기능 + 관리자 페이지
- MySQL 전환 및 AWS 배포 (EC2 + RDS)
