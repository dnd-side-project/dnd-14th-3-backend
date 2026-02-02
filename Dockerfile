# ==========================================
# 1. Build Stage (빌드 단계)
# ==========================================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

RUN ./gradlew dependencies
RUN chmod +x ./gradlew

COPY src ./src
RUN ./gradlew clean build -x test

# ==========================================
# 2. Run Stage (실행 단계)
# ==========================================
FROM eclipse-temurin:17-jre-alpine-3.23

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
