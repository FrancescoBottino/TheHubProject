FROM gradle:8.9-jdk21 AS builder
LABEL stage=builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/home/gradle/.gradle,uid=1000,gid=1000 \
    --mount=type=cache,target=/home/gradle/.konan,uid=1000,gid=1000 \
    gradle :server:buildFatJar --no-daemon -Penvironment=dev

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /home/gradle/project/server/build/libs/server-all.jar ktor-server.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]