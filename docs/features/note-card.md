# Feature: Notiz-Vorschau (NoteCard)

Die `NoteCard`-Komponente dient der kompakten Darstellung von Notizen in der Listenansicht. Sie bietet eine Vorschau des Inhalts und visualisiert Metadaten wie Sichtbarkeit und Datum.

## 🛠 Technische Umsetzung

- **Komponente:** `NoteCard.vue`
- **Markdown-Parsing:** Einsatz der Bibliothek `marked`, um Markdown-Inhalte (Titel und Inhalt) in HTML umzuwandeln.
- **Spezial-Rendering:** - Ein angepasster `marked.Renderer` sorgt dafür, dass komplexe Medien (Videos oder Bilder) in der Vorschau nicht direkt geladen werden.
    - Stattdessen werden platzsparende Platzhalter (Icons und Text-Links) gerendert, um die Performance der Listenansicht zu optimieren.
- **Inhaltskürzung (Truncation):** Das `truncatedContent` computed property begrenzt den Text auf 90 Zeichen, um ein einheitliches Karten-Layout zu gewährleisten.
- **Navigation:** Die gesamte Karte ist klickbar und leitet den Nutzer über den Vue Router zur Detailansicht (`/notes/:id`).



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS durch `v-html`** | Da die Komponente `v-html` nutzt, um das gerenderte Markdown anzuzeigen, besteht ein hohes Risiko für Script-Injection. **Vorbeugung:** Alle Inhalte müssen zwingend serverseitig oder vor dem Parsing bereinigt werden (DOMPurify). |
| **Media-Injection** | Angreifer könnten versuchen, bösartige Skripte über Bild-URLs oder Video-Embeds einzuschleusen. |
| **Vorschau-Logik** | Durch den Spezial-Renderer werden `embed:` und `image-embed:` Tags hart codiert in harmlose `<span>`-Elemente umgewandelt, wodurch die Ausführung von eingebetteten Skripten in der Vorschau verhindert wird. |



## 🔒 Datenschutz

- **Sichtbarkeits-Indikatoren:** Die Komponente visualisiert sofort, ob eine Notiz "Privat" (Schloss-Icon) oder "Öffentlich" (Weltkugel-Icon) ist. Dies verhindert, dass Nutzer versehentlich sensible Informationen öffentlich teilen.
- **Daten-Integrität:** In der Vorschau werden keine externen Tracker geladen (z.B. keine direkten YouTube-Embeds oder Bilder von fremden Servern), was die Privatsphäre des Nutzers beim Scrollen durch die Liste schützt.

---

## 🎨 UI/UX Design
- **Interaktives Feedback:** Beim Hovern über die Karte ändert sich der Rahmen und ein "Details ansehen"-Pfeil erscheint (Group-Hover Effekt).
- **Line-Clamping:** Mittels CSS (`-webkit-line-clamp`) wird sichergestellt, dass der Titel und der Text niemals mehr als drei Zeilen einnehmen, egal wie lang der eigentliche Inhalt ist.