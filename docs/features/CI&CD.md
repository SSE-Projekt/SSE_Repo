# Dokumentation: Implementierung einer DevSecOps CI/CD-Pipeline

Dieses Dokument beschreibt die Architektur und Umsetzung der automatisierten CI/CD-Pipeline. Das Hauptziel ist die Sicherstellung der Softwareintegrität sowie die Einhaltung von Sicherheitsstandards durch automatisierte Prüfzyklen (Shift-Left Security).

## 🚀 Methodik der CI/CD-Pipeline

Um eine plattformunabhängige Ausführung des Codes zu garantieren, wurden separate Workflows für das Frontend und Backend implementiert.



### 1. Build-Automatisierung und Umgebungskonsistenz
* **Backend-Build:** Verwendung von **Maven** zur Kompilierung und Ausführung von Unit-Tests in einer isolierten Umgebung.
* **Frontend-Build:** Nutzung von **Node.js** und **Vite** zur Erstellung eines produktionsbereiten Bundles.
* **Synchronisation:** Einführung einer strikten Namenskonvention (Case Sensitivity), um Diskrepanzen zwischen Windows-Entwicklungsumgebungen und Linux-basierten Build-Servern zu eliminieren.

### 2. Isolierte Teststrategie
Ein wesentlicher Schritt war die Entkopplung der Tests von externen Abhängigkeiten:
* **H2-Datenbank:** Integration einer In-Memory-Datenbank (H2) für die Testphase.
* **Profil-Steuerung:** Konfiguration spezifischer Properties, damit Tests unabhängig von einer persistenten Datenbankinstanz (Supabase) erfolgreich durchlaufen können.

---

## 🛡️ DevSecOps: Sicherheitsintegration

Die Sicherheit ist fest in den Bereitstellungsprozess integriert, um Schwachstellen bereits vor dem Release zu identifizieren.

### Containerisierung mit Docker
Die Anwendung wird mithilfe von **Docker** containerisiert. Dies stellt sicher, dass alle Abhängigkeiten in einem unveränderlichen Artefakt gekapselt sind und die Ausführung über den gesamten Lebenszyklus konsistent bleibt.

### Vulnerability Scanning mit Trivy
Als zentrales Sicherheits-Gate wurde der Scanner **Trivy** integriert:
* **Automatisierter Scan:** Jedes erstellte Docker-Image wird automatisch auf bekannte Schwachstellen (CVEs) untersucht.
* **Security Gate:** Die Pipeline ist so konfiguriert, dass der Prozess bei Funden mit hohem Kritikalitätsgrad (High/Critical) sofort gestoppt wird. Dies schützt die Software-Supply-Chain aktiv.



---

## 🛡️ Schwachstellen & Vorbeugung

| Prozessschritt | Sicherheitsmaßnahme | Ziel |
| :--- | :--- | :--- |
| **Build-Phase** | Isolierte Umgebungen | Ausschluss von "Man-in-the-Middle"-Angriffen während der Kompilierung. |
| **Test-Phase** | H2 In-Memory DB | Schutz von Produktionsdaten; keine暴露 von Datenbank-Credentials in Test-Logs. |
| **Release-Phase** | Trivy Image Scanning | Verhindert das Deployment von Containern mit bekannten Sicherheitslücken. |

## 🔒 Datenschutz & Integrität

* **Geheimnisverwaltung:** Alle API-Keys und Datenbank-Passwörter für Supabase werden während der Pipeline über verschlüsselte Umgebungsvariablen (Secrets) injiziert.
* **Plattformunabhängigkeit:** Durch die Docker-Kapselung wird sichergestellt, dass keine sicherheitsrelevanten Fehlkonfigurationen des Host-Systems die Anwendung beeinflussen.

---

## 💡 Fazit
Durch die Implementierung dieser Pipeline wurde der Entwicklungsprozess von einer manuellen Bereitstellung zu einem automatisierten, sicherheitsorientierten Workflow überführt. Die technische Konsistenz und die automatisierte Erkennung von Schwachstellen bilden das Fundament für ein sicheres Software-Release-Management.