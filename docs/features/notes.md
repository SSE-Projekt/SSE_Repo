# Feature: Notizen erstellen und bearbeiten

Diese Kernfunktion ermöglicht es Nutzern, Inhalte sicher zu speichern, zu kategorisieren und bestehende Einträge zu aktualisieren.

## 🛠 Technische Umsetzung

### Erstellungsprozess
- **Komponente:** `entryCard.vue`
- **Datenfluss:** Ein reaktives Vue-Objekt fängt Titel, Inhalt und Kategorie auf. Beim Speichern wird eine `POST`-Anfrage an `/api/notes` gesendet.
- **Feedback:** Nach erfolgreichem Speichern wird der Nutzer mittels `router.push` zurück zur Übersicht geleitet.

### Bearbeitungsprozess
- **Komponente:** `EditNoteView.vue`
- **Navigation:** Die ID der Notiz wird als URL-Parameter übergeben. Beim Laden der Seite werden die aktuellen Daten via `GET`-Anfrage abgerufen.
- **History-Management:** Nach dem Speichern wird `router.replace` verwendet, um den Browserverlauf sauber zu halten und zu verhindern, dass der "Zurück"-Button den Nutzer erneut auf das Bearbeitungsformular führt.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS in Notizinhalten** | Da Notizen oft Sonderzeichen enthalten, ist `DOMPurify` hier kritisch. Jeder Titel und jeder Textkörper wird vor dem Speichern und vor der Anzeige bereinigt. |
| **Insecure Direct Object Reference (IDOR)** | (Backend-Schutz) Das System prüft bei jedem `GET` oder `PUT` Request auf eine Notiz-ID, ob die `user_id` des anfragenden JWT-Tokens mit dem Besitzer der Notiz übereinstimmt. |
| **Datenverlust** | Implementierung von "Cancel"-Buttons und Validierung der Pflichtfelder, um leere Notizen zu verhindern. |

## 🔒 Datenschutz

### Sensibilität der Daten
Notizen enthalten oft die privatesten Informationen der Nutzer. Daher gelten folgende Regeln:
- **Keine Indizierung:** Notizen sind privat und werden nicht für Suchmaschinen bereitgestellt.
- **Inhalts-Integrität:** Die Daten werden so gespeichert, wie der Nutzer sie verfasst hat, jedoch gereinigt von schädlichen Skripten.
- **Löschkonzept:** Wenn ein Nutzer eine Notiz löscht, wird sie (je nach Backend-Logik) entweder als `deleted` markiert oder vollständig aus der Datenbank entfernt, um dem "Recht auf Vergessenwerden" nachzukommen.



---

## 💡 UX-Besonderheiten
- **Kategorisierung:** Nutzer können Notizen Labels zuordnen, um die Organisation zu verbessern.
- **Echtzeit-Validierung:** Felder werden bereits während der Eingabe auf Länge und nach klicken auf Speichern auf unzulässige Zeichen geprüft.