# 1. Gradle 빌드 스테이지
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 의존성 다운로드를 위한 파일만 먼저 복사 (캐싱 최적화)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 소스코드가 변해도 build.gradle이 안 변하면 이 단계는 캐시(재사용)됨
RUN gradle dependencies --no-daemon

# 실제 소스코드 복사 및 빌드
COPY src ./src
RUN gradle clean build -x test --no-daemon

# 2. 실제 실행 스테이지 (경량화)
FROM openjdk:17-jdk-slim
WORKDIR /app

# 한국 시간대 설정 (클라우드 환경에서 시간 틀어짐 방지)
ENV TZ=Asia/Seoul

# builder 스테이지에서 만들어진 jar 파일만 복사
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

# 환경변수로 실행 프로필(dev/prod)을 주입받을 수 있도록 ENTRYPOINT 개조
ENTRYPOINT["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}", "-jar", "app.jar"]