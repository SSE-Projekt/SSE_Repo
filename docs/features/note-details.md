# Feature: Notiz-Details & Interaktionen (API-Anbindung)

Die Detailansicht ermöglicht das vollständige Lesen einer Notiz inklusive gerenderter Medieninhalte sowie die Verwaltung (Teilen, Bearbeiten, Löschen) über eine gesicherte API-Schnittstelle.

## 🛠 Technische Umsetzung

- **Komponente:** `NoteDetailView.vue`
- **Dynamic Routing & Data Fetching:**
  - Die Komponente empfängt eine `id` als Prop über den Vue Router.
  - **API-Integration:** Beim Laden (`onMounted`) werden die Daten asynchron über den Service `getNote(props.id)` von einer **selbst programmierten API (Spring Boot)** abgerufen, die direkt mit **Supabase** kommuniziert.
- **Markdown & Media Rendering:**
  - Einsatz von `marked` zur Umwandlung von Markdown in HTML.
  - **Spezial-Renderer:** Ein benutzerdefinierter Renderer erkennt das Präfix `embed:` in Bild-Syntax-Links, extrahiert die YouTube-Video-ID mittels Regex und wandelt diese in ein sicheres `iframe` um.
- **Echtes Sharing-System:**
  - Ein integriertes Modal lädt eine dynamische Benutzerliste vom Server via `getAllUsers()`.
  - Die Methode `shareWith(user)` sendet einen Request an den `SharedNoteController`, um die Notiz sicher für andere E-Mail-Adressen freizugeben.
- **Kontextbasierte UI:** Über `route.query.from` wird geprüft, ob der Nutzer von seinen eigenen Notizen kommt. Nur dann werden die Aktions-Buttons (Bearbeiten, Löschen, Teilen) eingeblendet.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS via `v-html` (Iframe)** | Da YouTube-Embeds `iframe`-Tags benötigen, ist das Risiko hoch. |
| **Vorbeugung** | **DOMPurify mit Whitelist:** Das gerenderte HTML wird durch `DOMPurify.sanitize` bereinigt. Nur spezifische Attribute wie `allowfullscreen` und die Domain `youtube-nocookie.com` sind erlaubt. |
| **Unbefugtes Löschen / IDOR** | Ein Nutzer manipuliert die URL/ID, um eine fremde Notiz zu löschen. |
| **Vorbeugung** | **Backend-Sicherung:** Das Spring Boot Backend prüft bei jedem Löschbefehl die Eigentümerschaft (`owner_id`) gegen den JWT-Token des Nutzers. |



## 🔒 Datenschutz

- **YouTube No-Cookie:** Eingebettete Videos nutzen die Domain `www.youtube-nocookie.com`, um das Setzen von Tracking-Cookies zu verhindern, bevor der Nutzer das Video aktiv startet.
- **Dynamische Benutzerfilterung:** In der Benutzerliste für das Teilen wird der aktuell angemeldete Nutzer automatisch gefiltert, um "Selbst-Teilen" zu vermeiden.
- **Sichtbarkeits-Status:** Die UI zeigt explizit an, ob die Notiz als "Privater Link" oder "Öffentlich" in der Datenbank markiert ist.
- **Sicheres Löschen:** Beim Löschen wird eine Bestätigung (`confirm`) abgefragt, um versehentlichen Datenverlust zu vermeiden.

---

## 💡 UX & UI Details
- **Backdrop-Blur:** Das Teilen-Modal nutzt `backdrop-blur-sm` und eine `fade`-Transition für ein fokussiertes Nutzererlebnis.
- **Responsive Media:** Der `.video-container` sorgt über CSS-Tricks (`padding-bottom: 56.25%`) dafür, dass Videos immer im 16:9 Format bleiben, unabhängig von der Bildschirmgröße.
- **User Feedback:** Eine "Slide-up" Toast-Benachrichtigung (SnackBar) bestätigt erfolgreiche Aktionen wie das Teilen oder Löschen.