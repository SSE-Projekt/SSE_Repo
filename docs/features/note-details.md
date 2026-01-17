# Feature: Notiz-Details & Interaktionen

Die Detailansicht ermöglicht das vollständige Lesen einer Notiz inklusive gerenderter Medieninhalte (Markdown, Videos) sowie die Verwaltung der Notiz (Teilen, Bearbeiten, Löschen).

## 🛠 Technische Umsetzung

- **Komponente:** `NoteDetailView.vue`
- **Dynamic Routing:** Die Komponente empfängt eine `id` als Prop über den Vue Router, um die entsprechende Notiz aus dem Datenspeicher (`durch eine selbst programmierte API mit Datenbank` oder API) zu laden.
- **Markdown & Media Rendering:**
    - Einsatz von `marked` zur Umwandlung von Markdown in HTML.
    - **Spezial-Renderer:** Ein benutzerdefinierter Renderer erkennt das Präfix `embed:` in Bild-Syntax-Links, extrahiert die YouTube-Video-ID mittels Regex und wandelt diese in ein sicheres `iframe` um.
- **Teilen-Funktion:** Ein integriertes Modal erlaubt es einem Autor, seine Notizen mit anderen registrierten Nutzern zu verknüpfen (simuliert über die `otherUsers` Liste).
- **Kontextbasierte UI:** Über `route.query.from` wird geprüft, ob der Nutzer von seinen eigenen Notizen kommt. Nur dann werden die Aktions-Buttons (Bearbeiten, Löschen, Teilen) eingeblendet.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS via `v-html` (Iframe)** | Da YouTube-Embeds `iframe`-Tags benötigen, ist das Risiko hoch. |
| **Vorbeugung** | **DOMPurify mit Whitelist:** Das gerenderte HTML wird durch `DOMPurify.sanitize` bereinigt. Nur spezifische Attribute wie `allowfullscreen` und die Domain `youtube-nocookie.com` sind erlaubt. |
| **Malicious Video IDs** | Versuch, bösartigen Code in die Video-URL einzuschleusen. |
| **Vorbeugung** | **Regex-Validierung:** Die extrahierte Video-ID wird mit `/^[a-zA-Z0-9_-]+$/` geprüft, um sicherzustellen, dass keine Sonderzeichen für Injektionen genutzt werden. |
| **Unbefugtes Löschen** | Ein Nutzer manipuliert die URL, um eine Notiz zu löschen, die ihm nicht gehört. |
| **Vorbeugung** | Das UI prüft den Kontext (`from === 'my-notes'`). (Empfehlung: Serverseitige Prüfung der Eigentümerschaft vor dem Löschvorgang). |



## 🔒 Datenschutz

- **YouTube No-Cookie:** Eingebettete Videos nutzen die Domain `www.youtube-nocookie.com`, um das Setzen von Tracking-Cookies durch Google zu verhindern, bevor der Nutzer das Video aktiv startet.
- **Sichtbarkeits-Status:** Die Komponente zeigt deutlich an, ob die Notiz über einen "Privaten Link" oder "Öffentlich" erreichbar ist.
- **Sicheres Löschen:** Beim Löschen wird eine Bestätigung (`confirm`) abgefragt, um versehentlichen Datenverlust zu vermeiden.

---

## 💡 UX & UI Details
- **Backdrop-Blur:** Das Teilen-Modal nutzt `backdrop-blur-sm` und eine `fade`-Transition für ein fokussiertes Nutzererlebnis.
- **Responsive Media:** Der `.video-container` sorgt über CSS-Tricks (`padding-bottom: 56.25%`) dafür, dass Videos immer im 16:9 Format bleiben, unabhängig von der Bildschirmgröße.
- **User Feedback:** Eine "Slide-up" Toast-Benachrichtigung bestätigt erfolgreiche Aktionen wie das Teilen oder Löschen.