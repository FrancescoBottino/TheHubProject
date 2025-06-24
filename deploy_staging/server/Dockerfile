FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY deploy_staging/server/output/ktor-server.jar ktor-server.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]