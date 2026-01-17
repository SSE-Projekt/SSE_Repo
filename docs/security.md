# Sicherheit und Datenschutz

Dieses Dokument beschreibt die globalen Sicherheitsmaßnahmen und Datenschutz-Prinzipien, die in der gesamten Applikation angewendet werden.

## 🛡️ Sicherheitsarchitektur

### 1. Cross-Site Scripting (XSS) Prävention
Um die Einschleusung von bösartigem Code zu verhindern, setzen wir auf eine mehrschichtige Verteidigung:
- **Sanitizing:** Alle Benutzereingaben werden mit `DOMPurify` bereinigt, bevor sie verarbeitet oder versendet werden.
- **Input Filtering:** Kritische Felder (wie Passwörter oder Nutzername) werden zusätzlich durch reguläre Ausdrücke (Regex) gefiltert, um gefährliche Zeichen wie `< > ' " ( ) -` von vornherein auszuschließen.

### 2. Authentifizierung & Autorisierung
Die Sicherheit der Nutzersitzungen wird durch moderne Standards gewährleistet:
- **JWT (JSON Web Tokens):** Nach dem Login wird ein verschlüsselter Token im `Datenank` gespeichert. Dieser Token wird bei jedem API-Aufruf im Authorization-Header mitgesendet.
- **Role-Based Access Control (RBAC):** Die Anwendung unterscheidet strikt zwischen Rollen (z.B. Leser vs. Autor). Funktionen wie das Erstellen von Notizen werden im Frontend (UI-Elemente ausgeblendet) und im Backend (Route-Guards) geschützt.



### 3. Passwort-Sicherheit
- **Komplexität:** Wir erzwingen eine Mindestlänge von 8 Zeichen, Groß-/Kleinschreibung, Zahlen und Sonderzeichen.
- **Hashing:** (Backend-Hinweis) Passwörter werden niemals im Klartext gespeichert, sondern mit einem modernen Hashing-Algorithmus (z.B. BCrypt) serverseitig verschlüsselt.

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