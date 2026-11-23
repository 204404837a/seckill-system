FROM maven:3-eclipse-temurin-8 AS build

WORKDIR /build
COPY pom.xml ./
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn --batch-mode --no-transfer-progress -DskipTests package

FROM eclipse-temurin:8-jre-jammy

WORKDIR /app
COPY --from=build /build/target/hm-dianping-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
