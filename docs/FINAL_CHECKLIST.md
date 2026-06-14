# DevClass P3 최종 체크리스트

| 구분 | 구현 내용 | 확인 위치 |
|---|---|---|
| P1 | 강의 CRUD, 검색/정렬/페이징, 수강, 진도, MySQL/Flyway | Course/Enrollment/Progress API |
| P2 | JWT, STUDENT/INSTRUCTOR/ADMIN 권한, 신고, 알림 | SecurityConfig, 관리자 화면 |
| 관리자 | 사용자 활성/비활성, 신고 강의 삭제, 강의 승인/숨김, 운영 지표 | `/admin`, `/api/admin/**` |
| Q&A | 수강생 질문 등록, 강사 질문 조회/답변, 알림 | 강의 상세, 강사 센터 |
| 구독 | 강사 구독/해제, 새 강의 앱/이메일 알림 | SubscriptionService |
| 스케줄러 | 인기 강의 집계, 오래된 스냅샷 정리, 실행 이력 | DevClassScheduler |
| Webhook | HMAC 서명, 이벤트 중복 방지, 수신 이력 | `/api/webhooks/payment` |
| 관측성 | JSON 로그, 요청 ID/응답시간, Health, Prometheus | Actuator, logback |
| 보안 | JPA 바인딩, 입력 검증, React 이스케이프, JWT/RBAC | 백엔드/프론트 공통 |
| 성능 | DB 인덱스, 페이징, Next 정적 최적화 | V8~V10 migration, Next build |
| 결제 | 결제 준비/승인/취소, 결제 내역 저장 | Payment API |
| 배포 | Docker, Compose, GitHub Actions, Vercel 환경 변수 | 각 저장소 배포 파일 |

## 발표 자료 필수 항목

- 배포 주소와 Health 응답
- 역할별 학생/강사/관리자 데모 계정
- Q&A, 구독 알림, 관리자 승인/숨김 시연
- 스케줄러 작업 이력과 구조화 로그
- DB 인덱스 및 프론트 빌드 크기 개선 내용
- 장애/보안 대응과 환경 변수 관리 방식
