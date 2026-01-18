# Willkommen bei Secure Notes

**Secure Notes** ist eine moderne, webbasierte Plattform zur sicheren Verwaltung persönlicher und geteilter Notizen. Dieses Projekt wurde im Rahmen einer technischen Leistungsfeststellung entwickelt, mit einem besonderen Fokus auf **Cybersecurity**, **Clean Code** und **datenschutzkonforme Implementierung**.



## 🎯 Projektziel

Das Hauptziel von *Secure Notes* ist es, eine Anwendung bereitzustellen, die intuitiv bedienbar ist, aber gleichzeitig robust gegen gängige Webschwachstellen (gemäß OWASP Top 10) geschützt bleibt.

Besonderes Augenmerk liegt auf:
- **Prävention von XSS:** Durch striktes Sanitizing aller Benutzereingaben.
- **Sicheres Passwort-Management:** Durch Komplexitätsregeln und geschützte Reset-Flows.
- **Rollenbasierte Zugriffskontrolle:** Klare Trennung zwischen Lesern und Autoren.

---

## 🛠 Technology Stack

Wir nutzen einen modernen **Technology Stack**, um Performance und Sicherheit zu garantieren:

| Ebene | Technologie |
| :--- | :--- |
| **Frontend** | Vue.js 3 (Composition API) |
| **Backend** | **Spring Boot** mit **Maven** |
| **Datenbank** | **Supabase** (PostgreSQL) |
| **Infrastruktur** | **Docker** (Containerisierung) |
| **Sicherheit** | JWT Token & **RLS Policies** (Row Level Security) [DOMPurify](https://github.com/cure53/dompurify), Regex-Validierung|
| **Markdown Parsing** | [Marked.js](https://marked.js.org/) |
| **Dokumentation** | MkDocs mit Material Theme |


## 🎯 Kernfokus: Sicherheit
Das Projekt implementiert eine "Defense-in-Depth"-Strategie:
1. **Frontend-Ebene:** Bereinigung durch DOMPurify.
2. **Übertragung:** JWT-Authentifizierung.
3. **Datenbank-Ebene:** **RLS (Row Level Security)** in Supabase sorgt dafür, dass Daten auf Zeilenebene geschützt sind.

---

## 🛠 Hauptfunktionen

Die Applikation ist in verschiedene Module unterteilt:

1.  **Authentifizierung:** Sicherer Login und Registrierung mit Rollenauswahl.
2.  **Notiz-Verwaltung:** Erstellen, Bearbeiten und Löschen von Notizen mit Markdown-Support.
3.  **Medien-Integration:** Sicheres Einbetten von YouTube-Videos (No-Cookie-Standard).
4.  **Sharing-System:** Privatsphäre-Einstellungen (Privat/Öffentlich) und gezieltes Teilen mit anderen Nutzern.
5.  **Suche & Filter:** Echtzeit-Suche mit URL-Synchronisation für optimale UX.

---

## 📖 Struktur der Dokumentation

In dieser Dokumentation findest du detaillierte Informationen zu:

- **[Sicherheit & Datenschutz](security.md):** Globale Sicherheitskonzepte und DSGVO-Aspekte.
- **[Features](features/auth.md):** Technische Details und Schwachstellenanalysen zu jeder Funktion.

> **Hinweis für Prüfer:** Jede Feature-Seite enthält einen Abschnitt "Schwachstellen & Vorbeugung", in dem explizit auf die technische Umsetzung von Sicherheitsmaßnahmen eingegangen wird.

---

*Entwickelt als Demonstration für eine sichere Webarchitektur.*