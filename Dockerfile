FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradle gradle
COPY gradlew .
RUN chmod +x gradlew
COPY build.gradle .
COPY settings.gradle .

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew build --no-daemon -x test


FROM eclipse-temurin:21-jre-alpine AS executor

WORKDIR /app

COPY --from=builder /app/build/libs/Finlog-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]