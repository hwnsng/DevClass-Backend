# ── Build stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY DevClass/gradlew .
COPY DevClass/gradle gradle
COPY DevClass/build.gradle .
COPY DevClass/settings.gradle .
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon -q

COPY DevClass/src src
RUN ./gradlew bootJar --no-daemon -q

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine
RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# uploads 볼륨 마운트 포인트
RUN mkdir -p /app/uploads && chown app:app /app/uploads
VOLUME ["/app/uploads"]

USER app
EXPOSE 8080

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
