# Etappe 1: Bauphase (Build-Stage)
# Wir nutzen ein Image mit Maven und JDK, um die Anwendung zu kompilieren
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Kopieren der Maven-Konfigurationsdatei
COPY pom.xml .

# Kopieren des Quellcodes
COPY src ./src

# Kompilieren des Projekts und Erstellen der .jar Datei
# Die Tests werden hier übersprungen (-DskipTests), um den Build-Prozess zu beschleunigen
RUN mvn clean package -DskipTests

# Etappe 2: Laufzeitphase (Run-Stage)
# Wir nutzen ein schlankes JRE-Image auf Alpine-Basis für die Ausführung
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Die fertig gebaute .jar Datei aus der Build-Stage kopieren
# Dies ist eine Sicherheitsmaßnahme (Multi-Stage Build), damit der Quellcode nicht im Image landet
COPY --from=build /app/target/*.jar app.jar

# Sicherheits-Best-Practice: Erstellen eines Non-Root-Users
# Die Anwendung wird nicht mit Root-Rechten ausgeführt, um das Risiko bei Container-Ausbrüchen zu minimieren
RUN addgroup -S ssegroup && adduser -S sseuser -G ssegroup
USER sseuser

# Startbefehl für die Spring Boot Anwendung
ENTRYPOINT ["java", "-jar", "app.jar"]