# backend/Dockerfile

# 1. 빌드(Build) 단계: Gradle을 사용하여 .jar 파일을 생성합니다.
FROM eclipse-temurin:21-jdk-jammy as builder
WORKDIR /app
COPY . .
# ./gradlew 실행 권한 부여
RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# 2. 실행(Runtime) 단계: 빌드된 .jar 파일만으로 가벼운 이미지를 만듭니다.
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 빌드 단계에서 생성된 .jar 파일을 복사합니다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 8080 포트를 외부에 노출합니다.
EXPOSE 8080

# 컨테이너가 시작될 때 애플리케이션을 실행합니다.
ENTRYPOINT ["java", "-jar", "app.jar"]