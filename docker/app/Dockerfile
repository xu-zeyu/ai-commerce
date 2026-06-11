FROM gradle:8-jdk17-jammy AS builder

ARG APP_MODULE
ARG APP_JAR_PATH

WORKDIR /workspace

COPY gradle gradle
COPY gradlew settings.gradle build.gradle ./
COPY buildSrc buildSrc
COPY ai-commerce-common ai-commerce-common
COPY ai-commerce-auth ai-commerce-auth
COPY ai-commerce-start ai-commerce-start
COPY jinHan-shop jinHan-shop

RUN chmod +x gradlew
RUN ./gradlew "${APP_MODULE}:bootJar" --no-daemon
RUN cp ${APP_JAR_PATH} /tmp/app.jar

FROM eclipse-temurin:17-jre-jammy

RUN groupadd --system spring \
    && useradd --system --gid spring --create-home --home-dir /app spring \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /tmp/app.jar /app/app.jar

USER spring

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -Dfile.encoding=UTF-8"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
