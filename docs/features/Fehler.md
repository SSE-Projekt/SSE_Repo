# Fehlerbehandlung & Sicherheitsvalidierung

Diese Seite beschreibt, wie die Anwendung auf fehlerhafte Eingaben, unbefugte Zugriffsversuche oder Verletzungen der Geschäftslogik reagiert. Das Ziel ist es, dem Nutzer hilfreiches Feedback zu geben und gleichzeitig die Integrität des Systems zu schützen.

## ⚠️ Validierungslogik & Fehlertypen

Die Anwendung nutzt ein mehrstufiges System, um Fehler abzufangen. Dabei wird zwischen einfachen Formularfehlern und kritischen Sicherheitswarnungen unterschieden.

### 1. Formular- und Logikfehler
Diese Fehler treten auf, wenn der Nutzer notwendige Felder übersieht oder die Erwartungen der UI nicht erfüllt.

| Ereignis | Reaktion der Applikation | UI-Komponente |
| :--- | :--- | :--- |
| **Leerer Titel** | "Titel fehlt" Fehlermeldung wird angezeigt. | SnackBar (Failed) |
| **Leerer Inhalt** | "Text Content fehlt" Fehlermeldung wird angezeigt. | SnackBar (Failed) |
| **Notiz nicht gefunden** | Umleitung auf eine Fehlerseite mit Link zum Dashboard. | NoteDetailView (v-else) |
| **Ungültiger Login** | Hinweis auf falsche Anmeldedaten ohne Details preiszugeben. | LoginView |



### 2. Sicherheitswarnungen (XSS & Link-Schutz)
Wenn das System potenziell gefährliche Inhalte erkennt, die durch `DOMPurify` oder unsere Regex-Filter abgefangen werden, erfolgt eine abgestufter Warnmechanismus.

* **Warnung (Potenzielles Risiko):** Tritt auf, wenn das System Inhalte bereinigt hat (z. B. unbekannte HTML-Tags entfernt) oder wenn ein Link nicht dem sicheren Format `(embed:)` oder `(image-embed:)` entspricht.
    * *Meldung:* "Potentielles Sicherheitsrisiko erkannt!"
* **Fehler (Kritisches Risiko):** Tritt auf, wenn die Bereinigung dazu führt, dass kein valider Inhalt mehr übrig bleibt (z. B. nur bösartiger Code eingegeben wurde).
    * *Meldung:* "Tatsächliches Sicherheitsrisiko erkannt und entfernt!"



---

## 🔒 Serverseitige Fehler & RLS-Blockaden

Dank der **Row Level Security (RLS)** in der Datenbank und der Logik im **Spring Boot Backend** werden unlogische Aktionen auf technischer Ebene blockiert, selbst wenn die UI umgangen wird.

### Szenarien für blockierte Aktionen:
1.  **Unbefugtes Bearbeiten:** Ein Nutzer versucht, die ID einer Notiz in der URL zu ändern, die ihm nicht gehört.
    * *Reaktion:* Die RLS-Policy `update the notes` gibt keinen Datensatz zurück oder das Backend verweigert die Transaktion.
2.  **Unbefugtes Löschen:** Ein Nutzer sendet einen Löschbefehl für eine fremde Notiz.
    * *Reaktion:* Die Datenbank prüft `auth.uid() = owner_id`. Da dies fehlschlägt, wird die Zeile nicht gelöscht.
3.  **Fremde geteilte Notizen:** Zugriff auf `shared_notiz` ohne Absender- oder Empfänger-ID zu sein.
    * *Reaktion:* Die Abfrage liefert ein leeres Ergebnis zurück (`404` oder leere Liste).

---

## 🛠 Technische Umsetzung der Fehlermeldungen

Die Fehlermeldungen werden zentral über die `SnackBar`-Komponente gesteuert, um ein konsistentes Design zu gewährleisten.

```javascript
// Beispiel für die Fehlersteuerung in der Logik
const handleWarn = (msg) => {
  snackbar.message = msg || 'Sicherheitsrisiko erkannt!';
  snackbar.type = 'warn'; // Gelbes Design
  snackbar.show = true;
};

const handleError = (msg) => {
  snackbar.message = msg || 'Fehler beim Speichern!';
  snackbar.type = 'failed'; // Rotes Design
  snackbar.show = true;
};