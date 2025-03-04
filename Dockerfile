# 빌드 단계: Gradle로 애플리케이션 빌드
FROM amazoncorretto:17-alpine AS build

WORKDIR /app

# 의존성 스크립트 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 권한 부여
RUN chmod +x ./gradlew

# 소스 코드 복사
COPY src src

# 의존성 설치 및 빌드
RUN ./gradlew build -x test --no-daemon

# 실행 단계: 빌드된 JAR 파일만 포함하는 최소 이미지
FROM amazoncorretto:17-alpine

WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8090

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
