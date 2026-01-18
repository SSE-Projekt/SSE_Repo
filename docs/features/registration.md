# Feature: Registrierung

Die Registrierung ermöglicht neuen Nutzern die Erstellung eines Kontos unter strikter Einhaltung von Sicherheitsregeln für Passwörter.

## 🛠 Technische Umsetzung

- **Komponente:** `Registrierung.vue`
- **Rollen-Auswahl:** Nutzer können zwischen der Rolle "Leser" (ID: 1) und "Autor" (ID: 2) wählen. Dies steuert die Berechtigungen in der gesamten Applikation.
- **Echtzeit-Validierung:** - Eine Regex-Prüfung (`emailRegex`) validiert das Format der E-Mail-Adresse beim Verlassen des Feldes (`@blur`).
    - Die Passwortstärke wird bei jedem Tastendruck (`@input`) neu berechnet.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **Schwache Passwörter** | Erzwingung von Komplexität: Mind. 8 Zeichen, Großbuchstaben, Zahlen und Sonderzeichen. |
| **Verbotene Zeichen** | **Sicherheits-Regex:** Symbole wie `< > ' " ( ) -` sowie Leerzeichen sind im Passwort untersagt, um Injektionsrisiken zu minimieren. |
| **XSS Injection** | `DOMPurify` wird auf das E-Mail-Feld angewendet. Bei Abweichungen zwischen Eingabe und bereinigtem Wert wird die Registrierung blockiert. |

## 🔒 Datenschutz

- **Passwort-Hashing:** Passwörter werden niemals im Klartext an die Datenbank gesendet.
- **Einwilligung:** Durch eine Checkbox (`agreement`) bestätigt der Nutzer aktiv, dass er die Berechtigungen seiner gewählten Rolle verstanden hat.
- **E-Mail Verifizierung:** Ein Hinweis informiert den Nutzer darüber, dass er eine Verifizierungs-E-Mail mit seinem Username erhält, um die Echtheit des Kontos zu bestätigen und durch diese zwei Faktoren Identifizierung mehr Sicherheit zu gewahrleisten.