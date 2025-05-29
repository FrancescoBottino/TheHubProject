#!/bin/bash
docker build -t my-ktor-server .
docker run --rm --env-file ./.env -p 8080:8080 -e PORT=8080 --name ktor-container my-ktor-server