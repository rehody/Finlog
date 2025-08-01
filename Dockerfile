FROM eclipse-temurin:21-jre-alpine
LABEL authors="michael"

WORKDIR /app

COPY . .
COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]