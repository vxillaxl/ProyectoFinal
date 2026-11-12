FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN --mount=type=cache,target=/root/.m2,sharing=locked mvn -B -DskipTests -pl user-dashboard-backend -am package

FROM eclipse-temurin:17-jre-jammy
RUN groupadd --system app \
    && useradd --system --gid app --uid 10001 --home-dir /app --shell /usr/sbin/nologin app
WORKDIR /app
COPY --from=build /src/user-dashboard-backend/target/user-dashboard-backend-1.0.0.jar /app/app.jar
USER 10001
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
