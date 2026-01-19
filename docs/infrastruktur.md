# Infrastruktur & Entwicklungsprozess

Dieses Dokument beschreibt die technologische Basis, die methodischen Abläufe und die Meilenstein-Planung, die bei der Entwicklung von **Secure Notes** zum Einsatz kamen.

---

## 🛠️ Entwicklungsumgebung (IDE)

Für eine konsistente Entwicklung über das gesamte Stack hinweg wurde folgende Umgebung genutzt:

* **Backend-Entwicklung:** **IntelliJ IDEA** zur Verwaltung des Spring Boot Frameworks und der Maven-Abhängigkeiten.
* **Frontend-Entwicklung:** **Visual Studio Code** für die reaktive Vue.js-Entwicklung.
* **Tools:** Einsatz von **Docker Desktop** zur lokalen Container-Orchestrierung und **Postman** zur Verifizierung der API-Endpunkte.

---

## 📋 Projektmanagement & GitHub-Workflows

Die Steuerung des Projekts erfolgte agil über GitHub, wobei der Fokus auf einer klaren Etappenziel-Planung lag.

### Meilenstein-Planung (Milestones)
Die Entwicklung wurde in fünf zentrale Phasen unterteilt, um den Fortschritt messbar zu machen:
1.  **Content-Backend-Einrichtung:** Aufbau der Kern-API und Datenbankstruktur.
2.  **Zugriffskontrolle-Backend-Einrichtung:** Implementierung von JWT-Authentifizierung und Row Level Security (RLS).
3.  **Frontend-Content:** Entwicklung der Benutzeroberfläche und Anbindung an die API-Services.
4.  **Testen:** Durchführung automatisierter Unit- und Integrationstests für Frontend und Backend.
5.  **Dockerisierung:** Finalisierung der Dockerfiles und Integration in die CI/CD-Pipeline.

### Feature-Branch Workflow
Um die Integrität des `main`-Branches zu schützen, wurde ein strikter Branching-Prozess angewandt:
* **Feature Branches:** Jede neue Funktion wurde isoliert in einem Branch entwickelt (z. B. `feature/access-control`).
* **Pull Requests (PR):** Vor dem Merge in den Hauptzweig wurde jeder Code-Abschnitt geprüft.
* **CI-Integration:** GitHub Actions validierte jeden PR automatisch durch Build- und Testläufe.



---

## 🤖 Einsatz von KI & Sicherheitsvorkehrungen

Künstliche Intelligenz wurde als Werkzeug zur Effizienzsteigerung eingesetzt, jedoch unter strengen Sicherheitsauflagen.

### KI-Dokumentation
* **Einsatzzweck:** Unterstützung beim Refactoring von Code, Generierung von Boilerplate-Code für Controller und Erstellung von komplexen Regex-Mustern für den Markdown-Renderer.
* **Verwendete Modelle:** ChatGPT / Gemini-AI.

### Sicherheitsmaßnahmen (KI-Governance)
* **Datenschutz:** Es wurden **keine** sensitiven Daten (API-Keys, Passwörter, echte Nutzerdaten) an KI-Modelle übermittelt.
* **Manuelle Validierung:** Jeder KI-generierte Code-Vorschlag wurde einem manuellen Security-Review unterzogen, um sicherzustellen, dass keine Schwachstellen (wie fehlerhafte Validierungen) übernommen wurden.
* **Integritätsprüfung:** Vorschläge für externe Abhängigkeiten wurden manuell auf ihre Existenz und Sicherheit geprüft, um "AI Hallucinations" vorzubeugen.

---

## 🛡️ CI/CD & Infrastruktur-Automatisierung

Die Pipeline bildet das Rückgrat für Qualität und Sicherheit:
* **Automatisierung:** Bei jedem Push werden die definierten Milestones durch automatisierte Tests validiert.
* **Container-Registry:** Automatisierter Build und Push der Images mittels der GitHub Action `docker/login-action@9780b0c442fbb1117ed29e0efdff1e18412f7567`.
* **Vulnerability Scanning:** Integration von **Trivy** in der Dockerisierungs-Phase, um bekannte Sicherheitslücken in Container-Images zu finden.



---

## 💡 Fazit
Durch die Strukturierung in **Milestones** wurde eine zielgerichtete Entwicklung ermöglicht, die von der ersten Backend-Einrichtung bis zur finalen Dockerisierung reichte. Die Kombination aus modernem Branching-Workflow und verantwortungsvollem KI-Einsatz garantiert eine hohe Softwarequalität bei gleichzeitiger Einhaltung strenger Sicherheitsstandards.