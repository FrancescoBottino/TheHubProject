FROM gradle:8.9-jdk21 AS builder
LABEL stage=builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN gradle :server:buildFatJar --no-daemon

FROM jetbrains/jbr:jbrre-21-linux-x64
WORKDIR /app
COPY --from=builder /home/gradle/project/server/build/libs/server-all.jar ktor-server.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]