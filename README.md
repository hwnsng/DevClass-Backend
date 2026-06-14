# DevClass P3

DevClass의 최종 운영형 프로젝트입니다. 학생 강의 탐색/수강/진도, JWT 역할 권한, 강사 강의 관리, 관리자 운영 기능에 구독, Q&A, 스케줄러, Webhook, 관측성, 결제를 통합합니다.

## 주요 기능

- 학생: 강의 검색/정렬, 장바구니/결제, 수강/진도/리뷰/북마크, 강사 구독, 강의 Q&A
- 강사: 강의/레슨 CRUD, 수강생 조회, 수강생 질문 답변
- 관리자: 사용자 활성화, 신고 강의 삭제, 강의 승인/숨김, 운영 지표와 배치 이력 조회
- 운영: MySQL/Flyway, JWT/RBAC, 이메일 알림, 스케줄러, 결제 Webhook, Actuator/Prometheus, JSON 로그, Docker, GitHub Actions

## 로컬 실행

1. `.env.example`을 기준으로 `.env`를 작성합니다.
2. `docker compose up --build -d`를 실행합니다.
3. 프론트 `http://localhost:3000`, API `http://localhost:8080/api`, Swagger `http://localhost:8080/swagger-ui.html`을 확인합니다.

## 운영 확인

- 헬스체크: `GET /api/health`, `GET /actuator/health`
- 메트릭: `GET /actuator/prometheus`
- 작업 이력: `GET /api/admin/jobs/runs`
- 결제 Webhook: `POST /api/webhooks/payment`

Webhook 서명을 사용하는 경우 요청 본문을 `PAYMENT_WEBHOOK_SECRET`으로 HMAC-SHA256 처리한 16진수 값을 `X-Webhook-Signature`에 전달합니다.

## 보안/성능

- JPA 바인딩과 고정 정렬 값으로 SQL Injection을 방지합니다.
- React 기본 출력 이스케이프를 사용하며 `dangerouslySetInnerHTML`을 사용하지 않습니다.
- 목록 API는 페이징하고 강의 상태/생성일, 수강, 신고, 알림, Q&A 인덱스를 적용합니다.
- JWT, DB, 결제, 메일, Webhook 비밀값은 환경 변수로만 주입합니다.
