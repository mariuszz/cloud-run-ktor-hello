FROM gradle:8.14.2-jdk21 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle installDist

FROM eclipse-temurin:21-jre
COPY --from=build /app/build/install/ktor-docker /app
EXPOSE 8080
CMD ["/app/bin/ktor-docker"]
