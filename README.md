![malrang_1](https://github.com/user-attachments/assets/90d5d8ca-67ad-4644-bdc1-8feb9b6450af)

---

![malrang_2](https://github.com/user-attachments/assets/7c699a85-244f-4011-858f-8dce0ea8b8fa)

---

# Malrang 프로젝트

## 프로젝트 개요
프로젝트 개요 Malrang 프로젝트는 글로벌 사용자가 시사 토론을 통해 다양한 관점을 공유할 수 있는 플랫폼입니다. 
사용자는 실시간으로 연결된 상대와 토론하며 다양한 주제를 논의할 수 있고, LLM 기반 AI 중재자가 흐름을 조율 또한, 사용자는 글로벌 토론을 통해 다양한 문화와 관점을 공유할 수 있는 환경을 제공합니다.
플랫폼은 다양한 언어와 주제를 제공하며, 대기열을 이용한 사용자 매칭과 친구 초대 기능을 통해 편리한 토론 환경을 조성합니다. 
이 프로젝트는 사용자 간 상호작용을 장려하며, 시사 토론을 통해 글로벌 문화와 관점을 공유할 수 있는 환경을 조성하는 것을 최종 목표로 했습니다.

## 주요 기능

### 1. **사용자 프로필 및 소셜 로그인**
   - 구글 계정을 이용한 로그인 및 프로필 설정 기능 제공
   - 프로필에는 사용자의 관심 시사 분야, 사용 언어, 닉네임, 매너 지수 평균 평점이 포함됨
   - **인증 방식**: Spring Security + OAuth 2.0 + JWT 기반 사용자 인증

### 2. **사용자 친구 목록 관리**
   - 사용자가 웹 서비스에 로그인한 모든 사용자 목록을 확인 가능
   - 사용자의 사용 언어, 관심 시사 분야에 따라 필터링하여 친구 추가 가능
   - 친구 추가된 사용자는 채팅방 내에서 초대할 수 있음
   - **기술**: RedisTemplate을 이용한 친구 요청, 초대, 상태 관리

### 3. **실시간 채팅**
   - 사용자의 관심 시사 분야와 사용 언어에 따라 채팅방 생성 및 분류
   - 매칭된 사용자 간 실시간 채팅 환경 제공
   - 웹 소켓 프로토콜을 이용한 실시간 통신 구현, 정의된 이벤트 핸들러로 연결 상태 관리

### 4. **언어 교환 매칭**
   - Deferred Result 클래스를 이용하여 서버의 대기열 관리와 Push, Pop을 구현하는 비동기 데이터 반환 기술 구현
   - 비동기 작업을 효율적으로 처리하기 위해 스레드 풀을 구성, 언어 설정 값에 따라 대기열을 그룹화
   - 채팅 요청부터 채팅 형성까지는 비동기로 대기, 채팅방이 형성되면 websocket으로 메시지를 주고 받도록 설계

### 5. **토론을 조율하는 AI의 중재자**
   - 총 6개 언어 지원 (한국어, 영어, 중국어, 일본어, 스페인어, 불어)
   - 채팅방의 설정에 따른 토론 간단한 주제 추천
   - 채팅방의 주제와 무관한 발언이 감지되면 사용자에게 경고 메시지 전송
   - 사용자가 작성한 메시지의 맞춤법과 문법을 자동으로 교정하여 가독성을 높임 (ON, OFF 가능)
   - 실시간으로 메시지를 분석해 공격적이거나 비하하는 언어를 감지하고 필터링

### 6. **사용자 평가 시스템**
   - 사용자가 채팅 종료 후 상대방의 매너 지수를 별점으로 평가할 수 있도록 설계
   - 평가 정보는 데이터베이스에 저장되며, 새로운 평가가 추가될 때마다 상대방의 평균 매너 지수가 자동으로 업데이트
   - 코드는 트랜잭션을 사용하여 평가 저장과 평균 지수 업데이트를 원자적으로 처리하여 데이터의 일관성을 유지

### 7. **프로덕션 배포와 CI/CD 파이프라인 구성**
   - AWS EC2(Ubuntu)를 활용한 서비스 배포
   - Spring Cloud Config을 위한 중앙 집중식 서버 설정과 데이터 암호화
   - GitHub Actions, Docker, Container Registry를 활용한 CI /CD 파이프 라인 구성
   - Testcontainers를 활용한 독립적 테스트 환경 구축 및 33개의 단위·통합 테스트 수
---

## 구현 기술 및 사용 언어 / 개발환경

### 프레임워크 및 라이브러리
- **개발 언어**: Java
- **프레임워크**: Spring Boot
- **레임워크 및 라이브러리**:
  - **REST API**: Spring Web MVC (REST API 구현)
  - **보안 프레임워크**: Spring Security
  - **JWT 라이브러리**: jjwt
  - **소셜 로그인 및 OAuth 2.0**: Spring Security OAuth
  - **웹소켓**: Spring Websocket (실시간 채팅 구현)
  - **웹 서버**: 내장형 서버 (Spring Boot 내장 Tomcat)

### 개발 환경
- **IDE**: IntelliJ IDEA
- **빌드 도구**: Gradle
- **버전 관리**: Git
- **테스트 프레임워크**: JUnit
- **API 문서화**: Swagger

### 배포 환경
- **서비스 플랫폼**: AWS EC2(Ubuntu), Docker
- **중앙 설정 서버**: Spring Cloud Config
- **컨테이너 이미지 저장소**: GitHub Container Registry
- **CI/CD**: GitHub Actions

### 데이터베이스
- **DBMS**: MySQL, Redis
- **데이터베이스 연동**: Spring Data JPA
---

## 개발 기간
- **10개월**
- **유우열** (단독 개발)

