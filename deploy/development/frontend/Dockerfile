FROM gradle:8.9-jdk21 AS builder
LABEL stage=builder
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle . .
RUN --mount=type=cache,target=/home/gradle/.gradle,uid=1000,gid=1000 \
    --mount=type=cache,target=/home/gradle/.konan,uid=1000,gid=1000 \
    gradle :composeApp:wasmJsBrowserDevelopmentExecutableDistribution --no-daemon -Penvironment=dev

FROM nginx:stable-alpine
RUN rm -rf /usr/share/nginx/html/*
COPY /deploy/development/frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /home/gradle/project/composeApp/build/dist/wasmJs/developmentExecutable /usr/share/nginx/html/
COPY --from=builder /home/gradle/project/deploy/development/frontend/assetlinks.json /usr/share/nginx/html/.well-known/assetlinks.json
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]