FROM eclipse-temurin:21-jdk-alpine
LABEL authors="michael"

WORKDIR /app

COPY . .
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]