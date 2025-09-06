FROM gradle:9.0.0-jdk21 AS build
WORKDIR /workspace
COPY --chown=gradle:gradle ../ .
RUN ./gradlew :ktor-docker:installDist --no-daemon

FROM eclipse-temurin:21-jre
ENV BUILD_MODE=DOCKER
COPY --from=build /workspace/ktor-docker/build/install/ktor-docker /app
EXPOSE 8080
CMD ["/app/bin/ktor-docker"]
