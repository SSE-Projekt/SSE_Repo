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

# FIX: OS-Pakete aktualisieren, um gnupg und libpng Lücken zu schließen
USER root
RUN apk update && apk upgrade --no-cache

# Die fertig gebaute .jar Datei aus der Build-Stage kopieren
# Dies ist eine Sicherheitsmaßnahme (Multi-Stage Build), damit der Quellcode nicht im Image landet
COPY --from=build /app/target/*.jar app.jar

# Sicherheits-Best-Practice: Erstellen eines Non-Root-Users
# Die Anwendung wird nicht mit Root-Rechten ausgeführt, um das Risiko bei Container-Ausbrüchen zu minimieren
# Non-Root User erstellen
RUN addgroup -S ssegroup && adduser -S sseuser -G ssegroup

# app.jar kopieren und direkt dem User sseuser zuweisen
COPY --from=build --chown=sseuser:ssegroup /app/target/*.jar app.jar

USER sseuser

ENTRYPOINT ["java", "-jar", "app.jar"]