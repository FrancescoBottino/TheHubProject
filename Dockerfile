FROM gradle:8.9-jdk21 AS builder
LABEL stage=builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper \
    gradle :server:buildFatJar --no-daemon --stacktrace

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /home/gradle/project/server/build/libs/server-all.jar ktor-server.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]