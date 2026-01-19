# Feature: Notizen erstellen und bearbeiten (Rich Media Support)

Diese Kernfunktion ermöglicht es Nutzern, Inhalte sicher zu speichern, zu kategorisieren und mit Medien wie Bildern oder Videos zu bereichern.

## 🛠 Technische Umsetzung

### Erstellung & Bearbeitung
- **Komponenten:** `entryCard.vue` (Erstellung) und `EditNoteView.vue` (Bearbeitung).
- **Markdown-Integration:** Die App unterstützt Markdown-Syntax für die Textformatierung.
- **Medien-Syntax:** Um Sicherheitsrisiken durch direktes HTML zu vermeiden, nutzt die App eine spezielle Syntax für eingebettete Medien:
    * **Bilder:** `![Titel](image-embed:URL)`
    * **Videos:** `![Titel](embed:YouTube-URL)`



### Rendering-Prozess
1. Der Nutzer gibt die Notiz mit der Spezial-Syntax ein.
2. Beim Speichern wird der Inhalt durch **DOMPurify** bereinigt.
3. In der Detailansicht extrahiert ein benutzerdefinierter **Markdown-Renderer** die Video-ID (z. B. von YouTube) und wandelt sie in ein sicheres, isoliertes `iframe` um.

## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS in Notizinhalten** | **Aktiv:** `DOMPurify` bereinigt jeden Titel und Textkörper vor dem Speichern und vor der Anzeige. |
| **Bösartige Video-Iframes** | **Regex-Validierung:** Nur validierte YouTube-IDs sind erlaubt. Das System blockiert unbekannte oder schädliche Quellen innerhalb des `embed:` Befehls. |
| **Insecure Direct Object Reference (IDOR)** | **Backend-Schutz:** Das System prüft bei jedem Request, ob die `user_id` des JWT-Tokens mit dem Besitzer der Notiz übereinstimmt. |
| **Unbefugtes Teilen** | **RLS Policies:** Row Level Security in der Datenbank stellt sicher, dass nur Besitzer (`owner_id`) oder autorisierte Empfänger Zugriff haben. |



## 🔒 Datenschutz

### Sensibilität & Medien
- **Keine Indizierung:** Notizen sind privat und für Suchmaschinen unsichtbar.
- **YouTube No-Cookie:** Eingebettete Videos werden über `youtube-nocookie.com` geladen, um das Tracking der Nutzer durch Drittanbieter-Cookies zu unterbinden.
- **Daten-Integrität:** Die Daten werden gereinigt von schädlichen Skripten gespeichert, wobei die ursprüngliche Formatierung des Nutzers erhalten bleibt.
- **Löschkonzept:** Ein Löschbefehl entfernt die Notiz gemäß der RLS-Policy dauerhaft aus der Datenbank ("Recht auf Vergessenwerden").

---

## 💡 UX-Besonderheiten
- **Vorschau-Logik:** Die spezialisierte Syntax erlaubt eine klare Trennung zwischen Textinhalt und Medienanhängen.
- **Echtzeit-Validierung:** Felder werden bereits während der Eingabe auf Länge und beim Speichern auf unzulässige Zeichen (XSS) geprüft.
- **Zustands-Management:** Nach dem Speichern wird `router.replace` verwendet, um den Browserverlauf sauber zu halten und doppelte Formularabsendungen zu verhindern.