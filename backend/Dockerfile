# Etappe 1: Bauphase
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Nur pom.xml kopieren und Dependencies laden (Cache-Vorteil)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 2. Source kopieren und bauen
COPY src ./src
RUN mvn clean package -DskipTests

# Etappe 2: Laufzeitphase
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Non-Root User erstellen
RUN addgroup -S ssegroup && adduser -S sseuser -G ssegroup

# app.jar kopieren und direkt dem User sseuser zuweisen
COPY --from=build --chown=sseuser:ssegroup /app/target/*.jar app.jar

USER sseuser

ENTRYPOINT ["java", "-jar", "app.jar"]