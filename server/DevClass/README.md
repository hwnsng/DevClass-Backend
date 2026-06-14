# DevClass Backend

Spring Boot 3.4, Java 21, MySQL, Flyway 기반 DevClass P3 API입니다.

## 실행

```bash
copy .env.example .env
docker compose up --build -d
```

- API: `http://localhost:8080/api`
- Swagger: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/api/health`
- Prometheus: `http://localhost:8080/actuator/prometheus`

운영 비밀값은 `.env` 또는 서버 환경 변수로만 주입합니다.
