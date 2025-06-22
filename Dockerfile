FROM gradle:8.14.2-jdk21 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle installDist

FROM eclipse-temurin:21-jre
COPY --from=build /app/build/install/cloud-run-ktor-hello /app
EXPOSE 8080
CMD ["/app/bin/cloud-run-ktor-hello"]
