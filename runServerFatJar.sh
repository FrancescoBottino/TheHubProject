#!/bin/bash
./gradlew :server:buildFatJar
set -a # automatically export all variables
source .env
set +a
java -jar server/build/libs/server-all.jar