# cloud-run-ktor-hello

A minimal HTTP service written in Kotlin using the [Ktor](https://ktor.io) framework, built with Gradle, containerized with Docker, and ready to be deployed to [Google Cloud Run](https://cloud.google.com/run).

This project is part of the "Kotlin + Cloud Run" blog series available at [zamolski.com](https://zamolski.com).

## Features

- Pure Kotlin (no Spring)
- Lightweight Ktor server
- Dockerfile-based deployment
- Cloud Run compatibility (port from `$PORT` env var)
- Gradle build setup

## Quick start

### Docker

```bash
./gradlew ktor-docker:build
docker build -f ktor-docker/Dockerfile -t cloud-run-ktor-hello .
docker run -p 8080:8080 cloud-run-ktor-hello
```

### JIB

```bash
./gradlew ktor-jib:jibDockerBuild
docker run -p 8080:8080 cloud-run-ktor-hello-local
```

Cloud run deployment:

```bash
cd ktor-docker
gcloud run deploy cloud-run-ktor-hello --source . --region europe-central2 --allow-unauthenticated
```