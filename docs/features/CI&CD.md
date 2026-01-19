# Dokumentation: Implementierung einer DevSecOps CI/CD-Pipeline

Dieses Dokument beschreibt die Architektur und Umsetzung der automatisierten CI/CD-Pipeline. Das Hauptziel ist die Sicherstellung der Softwareintegrität sowie die Einhaltung von Sicherheitsstandards durch automatisierte Prüfzyklen (Shift-Left Security).

## 📋 Anforderungen & Checkliste

* [x] **Automatische Dependency Updates**: Implementiert über automatisierte Tools (z.B. Dependabot).
* [x] **Vulnerability Warning**: Warnsystem für Sicherheitslücken in Abhängigkeiten.
* [x] **Automatisierte Tests**: Integration von Tests für Frontend und Backend.
* [x] **Container-Registry**: Automatisierter Build und Push der Docker-Container bei Main-Push und Releases.

---

## 🚀 Methodik der CI/CD-Pipeline

Um eine plattformunabhängige Ausführung des Codes zu garantieren, wurden separate Workflows für das Frontend und Backend implementiert.



### 1. Build-Automatisierung und Teststrategie
* **Backend (Java/Spring Boot):** Verwendung von **Maven** zur Kompilierung. Es ist mindestens ein automatisierter Unit-Test integriert, der in einer isolierten Umgebung mit einer **H2 In-Memory-Datenbank** ausgeführt wird, um Unabhängigkeit von der Produktionsdatenbank (Supabase) zu gewährleisten.
* **Frontend (Vue.js):** Nutzung von **Node.js** und **Vite**. Ein automatisierter Frontend-Test stellt die Integrität der Benutzeroberfläche vor dem Build-Prozess sicher.
* **Synchronisation:** Einführung einer strikten Namenskonvention (Case Sensitivity), um Diskrepanzen zwischen verschiedenen Betriebssystemen zu eliminieren.

### 2. Dependency Management & Security
* **Automatisierte Updates:** Das Projekt ist so konfiguriert, dass genutzte Bibliotheken (Dependencies) automatisch auf dem neuesten Stand gehalten werden.
* **Sicherheitswarnungen:** Ein automatisiertes Monitoring-System scannt die `pom.xml` (Backend) und `package.json` (Frontend) auf bekannte Schwachstellen in Drittanbieter-Bibliotheken und gibt bei Funden sofortige Warnungen aus.

---

## 🛡️ DevSecOps: Sicherheitsintegration & Registry

### Containerisierung & Registry-Push
Die Anwendung wird mithilfe von **Docker** containerisiert. Bei jedem Push auf den **Main Branch** sowie bei offiziellen **Releases** werden die Container automatisch gebaut und in einer Container-Registry gespeichert.
* **Authentifizierung:** Hierfür wird die GitHub Action `docker/login-action@9780b0c442fbb1117ed29e0efdff1e18412f7567` eingesetzt, um eine sichere Verbindung zur Registry zu gewährleisten.



### Vulnerability Scanning mit Trivy
Als zentrales Sicherheits-Gate wurde der Scanner **Trivy** integriert:
* **Automatisierter Scan:** Jedes erstellte Docker-Image wird vor dem finalen Speichern auf CVEs untersucht.
* **Security Gate:** Die Pipeline stoppt den Prozess sofort, falls Schwachstellen mit hohem Kritikalitätsgrad gefunden werden.

---

## 🛡️ Schwachstellen & Vorbeugung

| Prozessschritt | Sicherheitsmaßnahme | Ziel |
| :--- | :--- | :--- |
| **Dependency Check** | Automatisierte Scans | Früherkennung von Schwachstellen in externen Bibliotheken. |
| **Test-Phase** | Isolierte H2-Datenbank | Vermeidung von Datenlecks und Schutz von Credentials während der Testläufe. |
| **Registry-Push** | Token-basierte Auth | Sicherer Artefakt-Speicher durch verifizierte GitHub Actions. |
| **Release-Phase** | Trivy Image Scanning | Verhindert das Deployment von Containern mit bekannten Sicherheitslücken. |

## 🔒 Datenschutz & Integrität

* **Geheimnisverwaltung:** Alle sensiblen Daten werden während der Pipeline über verschlüsselte GitHub Secrets injiziert.
* **Supply-Chain-Schutz:** Durch die Kombination aus automatisierten Updates und Image-Scanning wird die gesamte Kette vom Code bis zum fertigen Container abgesichert.

---

## 💡 Fazit
Vom automatisierten Testen über das Scannen von Abhängigkeiten bis hin zum gesicherten Push in die Container-Registry: Diese Pipeline überführt den Entwicklungsprozess in einen modernen, sicherheitsorientierten Workflow. Dies garantiert, dass nur geprüfte und sichere Software-Artefakte veröffentlicht werden.