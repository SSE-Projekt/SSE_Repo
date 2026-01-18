# Sicherheit und Datenschutz

Dieses Dokument beschreibt die globalen Sicherheitsmaßnahmen und Datenschutz-Prinzipien, die in der gesamten Applikation angewendet werden.
## 🗄️ Datenbank-Sicherheit (Row Level Security - RLS)

Um sicherzustellen, dass Nutzer niemals Zugriff auf Daten anderer Nutzer erhalten, setzen wir auf **Row Level Security (RLS)** direkt in der PostgreSQL-Datenbank (Supabase).

In **Supabase** haben wir eine strikte **Row Level Security (RLS)** implementiert. Dies bedeutet, dass die Sicherheit nicht nur im Code, sondern direkt in der Datenbank verankert ist.
### Sicherheits-Policys
Jede Tabelle verfügt über strikte Zugriffsregeln:
- **Tabelle `users`:** Nutzer können alle Profile sehen (für das Teilen-Feature), aber nur ihr eigenes Profil bearbeiten oder löschen (`auth.uid() = id`).
- **Tabelle `notiz`:** Nur der Besitzer einer Notiz (`owner_id`) hat die Berechtigung, diese zu aktualisieren, einzufügen oder zu löschen.
- **Tabelle `shared_notiz`:** Diese Tabelle steuert den Zugriff auf geteilte Inhalte. Ein Zugriff ist nur erlaubt, wenn der Nutzer entweder der **Sender** oder der **Empfänger** der Notiz ist.



> **Sicherheits-Vorteil:** Selbst wenn ein Angreifer versucht, die API direkt aufzurufen oder den Frontend-Code zu manipulieren, blockiert die Datenbank jede Anfrage, die nicht durch eine RLS-Policy autorisiert ist.
## 🛡️ Sicherheitsarchitektur

### 1. Cross-Site Scripting (XSS) Prävention
Um die Einschleusung von bösartigem Code zu verhindern, setzen wir auf eine mehrschichtige Verteidigung:
- **Sanitizing:** Alle Benutzereingaben werden mit `DOMPurify` bereinigt, bevor sie verarbeitet oder versendet werden.
- **Input Filtering:** Kritische Felder (wie Passwörter oder Nutzername) werden zusätzlich durch reguläre Ausdrücke (Regex) gefiltert, um gefährliche Zeichen wie `< > ' " ( ) -` von vornherein auszuschließen.

### 2. Authentifizierung & Autorisierung - Backend-Sicherheit mit Spring Boot
Das Backend wurde mit **Spring Boot** entwickelt und nutzt **Maven** zur Abhängigkeitsverwaltung.
- **JWT (JSON Web Tokens):** Nach dem Login wird ein verschlüsselter Token im `Datenank` gespeichert. Dieser Token wird bei jedem API-Aufruf im Authorization-Header mitgesendet.
- **Role-Based Access Control (RBAC):** Die Anwendung unterscheidet strikt zwischen Rollen (z.B. Leser vs. Autor). Funktionen wie das Erstellen von Notizen werden im Frontend (UI-Elemente ausgeblendet) und im Backend (Route-Guards) geschützt.
- **Validierung:** Spring Boot validiert alle eingehenden Datenmodelle, bevor sie verarbeitet werden.


### 3. Passwort-Sicherheit
- **Komplexität:** Wir erzwingen eine Mindestlänge von 8 Zeichen, Groß-/Kleinschreibung, Zahlen und Sonderzeichen.
- **Hashing:** (Backend-Hinweis) Passwörter werden niemals im Klartext gespeichert, sondern mit einem modernen Hashing-Algorithmus (z.B. BCrypt) serverseitig verschlüsselt.

---

## Infrastruktur mit Docker

Durch den Einsatz von **Docker** wird die Anwendung in einer isolierten Umgebung ausgeführt. Dies minimiert Abhängigkeitskonflikte und erhöht die Sicherheit durch Container-Isolierung.

---
## 🔒 Datenschutz (GDPR / DSGVO)

Wir folgen dem Prinzip der **Datensparsamkeit**. Es werden nur Daten erhoben, die für den Betrieb der Anwendung absolut notwendig sind.

### Gespeicherte Daten
| Datentyp | Verwendungszweck | Schutzmaßnahme |
| :--- | :--- | :--- |
| **E-Mail Adresse** | Account-Identifizierung & Passwort-Reset | Verschlüsselte Übertragung (HTTPS) |
| **Nutzername** | Anzeige in der App & Login | Bereinigung durch DOMPurify |
| **Notizen** | Kernfunktion der App | Zugriff nur für autorisierte User / Rollen |

### LocalStorage Nutzung
Im Browser des Nutzers werden lediglich folgende Daten gespeichert:
- `token`: Der aktive Session-Token.
- `user`: Ein JSON-Objekt mit ID, Rolle und Nutzername zur UI-Personalisierung.

> **Hinweis:** Beim Logout werden alle Daten im `localStorage` mittels `localStorage.clear()` vollständig gelöscht, um unbefugten Zugriff an öffentlichen Rechnern zu verhindern.

---

## 🚀 Bekannte Schwachstellen & zukünftige Verbesserungen
- **Rate Limiting:** Aktuell gibt es keine strikte Begrenzung für Login-Versuche (Schutz gegen Brute-Force). Dies sollte in einer zukünftigen Version serverseitig implementiert werden.
- **Zwei-Faktor-Authentifizierung (2FA):** Zur Erhöhung der Sicherheit wäre die Implementierung von TOTP (Google Authenticator) ein sinnvoller nächster Schritt.