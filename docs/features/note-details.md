# Feature: Notiz-Details & Interaktionen (API-Anbindung)

Die Detailansicht ermöglicht das vollständige Lesen einer Notiz inklusive gerenderter Medieninhalte (Markdown, Videos) sowie die Verwaltung (Teilen, Bearbeiten, Löschen) über eine gesicherte API-Schnittstelle.

## 🛠 Technische Umsetzung

- **Komponente:** `NoteDetailView.vue`
- **Dynamic Routing & Data Fetching:**
  - Die Komponente empfängt eine `id` als Prop über den Vue Router.
  - **API-Integration:** Beim Laden (`onMounted`) werden die Daten asynchron über den Service `getNote(props.id)` von einer **selbst programmierten API (Spring Boot)** abgerufen, die direkt mit **Supabase** kommuniziert.
- **Markdown & Media Rendering:**
  - Einsatz von `marked` zur Umwandlung von Markdown in HTML.
  - **Spezial-Renderer:** Ein benutzerdefinierter Renderer erkennt die Präfixe `embed:` (für YouTube) und `image-embed:` (für Bilder) in der Bild-Syntax. Er extrahiert die Video-ID mittels Regex und wandelt diese in ein sicheres `iframe` um.
- **Aktions-Steuerung (Teilen, Bearbeiten, Löschen):**
  - Diese Funktionen sind **besitzergebunden** und kontextabhängig.
  - **Sichtbarkeits-Logik:** Über `route.query.from` wird geprüft, ob der Nutzer von seinen eigenen Notizen kommt. Nur bei `?from=my-notes` werden die Aktions-Buttons via `v-if` eingeblendet.
  - **Teilen:** 
    - Öffnet ein Modal mit einer dynamischen Benutzerliste via `getAllUsers()`.
    - Notiz über das weitergeben des Links(Link-Struktur https://localhost/#/my-app/notes/{document_id}) zu teilen
    - mithilfe des Links kann jeder angemeldete Benutzer die Notiz anzeigen, auch wenn diese privat ist.
  - **Löschen:** Führt nach einer `confirm()`-Bestätigung einen `DELETE`-Request an den Server aus.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle                    | Vorbeugung                                                                                                                                                                         |
|:---------------------------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **XSS via `v-html` (Iframe)**    | YouTube-Embeds benötigen `iframe`-Tags, was ein hohes Risiko für HTML-Injection darstellt.                                                                                         |
| **Vorbeugung (XSS)**             | **DOMPurify mit Whitelist:** Das HTML wird durch `DOMPurify.sanitize` bereinigt. Nur spezifische Attribute (`allowfullscreen`) und die Domain `youtube-nocookie.com` sind erlaubt. |
| **Unbefugtes Löschen/ Zugriff / IDOR** | Ein Nutzer manipuliert die URL oder sendet manuelle API-Requests, um fremde Notizen zu löschen oder zu lesen.                                                                         |
| **Vorbeugung (IDOR)**            | **Backend-Sicherung:** Das Spring Boot Backend prüft bei jedem Befehl die `owner_id` gegen den JWT-Token. Zudem greift die **Supabase RLS-Policy** (`auth.uid() = owner_id`).      |



## 🔒 Datenschutz

- **YouTube No-Cookie:** Eingebettete Videos nutzen `www.youtube-nocookie.com`, um das Setzen von Tracking-Cookies zu verhindern, bevor der Nutzer das Video aktiv startet.
- **Zugriffskontrolle:** RLS-Policies stellen sicher, dass geteilte Notizen nur für den Absender und den autorisierten Empfänger sichtbar sind.
- **Transparenz:** Die UI zeigt den Status ("Privater Link" vs. "Öffentlich") basierend auf dem Datenbankfeld `is_privat` deutlich an.

---

## 💡 UX & UI Details
- **Backdrop-Blur:** Das Teilen-Modal nutzt `backdrop-blur-sm` und eine `fade`-Transition für ein fokussiertes Nutzererlebnis.
- **Responsive Media:** Der `.video-container` sorgt über CSS (`padding-bottom: 56.25%`) dafür, dass Videos immer im 16:9 Format bleiben.
- **User Feedback:** Eine "Slide-up" Toast-Benachrichtigung (SnackBar) bestätigt erfolgreiche Aktionen wie das Teilen oder Löschen.