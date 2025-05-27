FROM openjdk:17-slim

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} ktor-server.jar

ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]