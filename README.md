
# SkillUp

스터디/모임을 생성하고, 참가 신청/승인/관리를 통해 함께 성장할 수 있는 **스터디 매칭 플랫폼**입니다.  
Spring Security, JWT, OAuth2, JPA 기반 백엔드 역량을 증명하기 위한 프로젝트입니다.

---

## 1. 프로젝트 개요

- 프로젝트명: SkillUp
- 개발 기간: 2025-12-09 ~ 2025-12-15 (총 7일)
  - Day1: 요구사항 정의 + ERD 설계
  - Day2~5: 백엔드 기능 개발
  - Day6~7: 프론트엔드 기능 개발

---

## 2. 구현 목표

- Spring Security + JWT 인증 및 인가 흐름 이해
- OAuth2 소셜 로그인(Naver 또는 Kakao)
- RESTful API 설계 및 Swagger 문서화
- Spring Data JPA 기반 CRUD/검색/페이징 처리
- Logback 기반 요청/응답/예외 로깅
- JUnit + AssertJ 기반 유닛 테스트 작성

---

## 3. 기술 스택

### Backend
| 항목 | 사용 기술 |
|------|---------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.8 |
| Security | Spring Security, OAuth2 Client, JWT |
| DB | MySQL |
| ORM | Spring Data JPA |
| API Docs | Springdoc OpenAPI(Swagger) |
| Build | Gradle |
| Test | JUnit5 + AssertJ |
| Logging | Logback |

### Frontend
| 항목 | 사용 기술 |
|------|---------|
| Language | JavaScript |
| Library | React 18 |
| Routing | React Router |
| HTTP | fetch API |
| Dev Tool | Vite |

---

## 4. 주요 기능

### 인증/인가
- 회원가입 / 로그인 / 로그아웃
- JWT Access Token + Refresh Token 발급 및 재발급
- OAuth2 기반 소셜 로그인
- 회원 탈퇴(Soft Delete)

### 사용자(User)
- 내 정보 조회/수정
- 생성한 스터디 / 참여중 / 신청중 / 북마크 목록 조회

### 스터디(Study)
- CRUD
- 검색 / 정렬 / 필터(태그, 지역, 모집중)
- 상세 조회

### 참가 관리
- 참가 신청/취소
- 스터디장 승인/거절 관리

### 북마크
- 등록/해제
- 북마크 목록 조회

---

## 5. 시스템 아키텍처

```
[React]  <---- HTTP(JSON, JWT) ---->  [Spring Boot API]  <---->  [MySQL]
  |                                         |
  |---- OAuth2 Redirect Flow(Provider) -----|
```

- 모든 기능은 REST API 호출 기반
- 인증 요청은 Authorization: Bearer <AccessToken> 방식
- Refresh Token으로 Access Token 재발급

---

## 6. 패키지 구조

```
com.skillup
 ├─ SkillUpApplication.java
 ├─ global
 │   ├─ config
 │   ├─ security
 │   ├─ exception
 │   ├─ common
 │   └─ logging
 ├─ domain
 │   ├─ user
 │   ├─ auth
 │   ├─ study
 │   ├─ token
 │   └─ ...
 └─ ...
```

---

## 7. ERD 개요

| Entity | 설명 |
|--------|------|
| User | 회원 |
| Study | 스터디 |
| StudyParticipant | N:M 중간 엔티티(참가 정보) |
| Tag | 태그 |
| StudyTag | Study-Tag N:M |
| StudyBookmark | 스터디 북마크 |
| RefreshToken | 토큰 관리 |

관계 요약
- User 1 : N Study(Owner)
- User N : M Study (Bookmark)
- Study N : M Tag
- Study 1 : N Participant
- User 1 : N RefreshToken

---

## 8. REST API 요약

### Auth
| Method | URL | 기능 |
|--------|-----|----|
| POST | /api/v1/auth/signup | 회원가입 |
| POST | /api/v1/auth/login | 로그인 |
| POST | /api/v1/auth/reissue | 토큰 재발급 |
| POST | /api/v1/auth/logout | 로그아웃 |

### User
| Method | URL | 기능 |
|--------|-----|----|
| GET | /api/v1/users/me | 내 정보 조회 |
| PATCH | /api/v1/users/me | 정보 수정 |
| DELETE | /api/v1/users/me | 탈퇴 |

### Study
| Method | URL | 기능 |
|--------|-----|----|
| POST | /api/v1/studies | 생성 |
| GET | /api/v1/studies | 목록 조회 |
| GET | /api/v1/studies/{id} | 상세 조회 |
| PATCH | /api/v1/studies/{id} | 수정 |
| DELETE | /api/v1/studies/{id} | 삭제 |

---

## 9. 실행 방법

### Backend

빌드
./gradlew clean build

실행
java -jar build/libs/skillup-0.0.1-SNAPSHOT.jar

Swagger 문서:  
http://localhost:8080/swagger-ui/index.html

### Frontend

npm install
npm run dev

환경 변수 예:
VITE_API_BASE_URL=http://localhost:8080

---

## 10. 테스트

- JUnit5 + AssertJ
- UserService / StudyService 중심 유닛 테스트 작성

---

## 11. 향후 개선 사항

- 알림 기능
- 스터디 내 게시판/댓글 기능
- 신고 기능 + 관리자 페이지
- AWS 배포(EC2 + RDS)
