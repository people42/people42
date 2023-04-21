FROM gradle:7.6.1-jdk11-alpine as build
WORKDIR /
COPY . .
RUN gradle build --no-daemon
RUN ls -l build/libs

FROM openjdk:11-jre-slim-buster
WORKDIR /
COPY --from=build /build/libs /
CMD ["java", "-jar", "soboro-0.0.1-SNAPSHOT.jar"]
