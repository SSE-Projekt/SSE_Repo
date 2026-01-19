# Feature: Suche und Filterung (SearchBar)

Die `SearchBar`-Komponente ist ein zentrales Steuerungselement, das es Nutzern ermöglicht, ihre Notizen-Sammlung effizient nach Stichworten zu durchsuchen und die Ansicht basierend auf der Sichtbarkeit (Privat vs. Öffentlich) zu filtern.

## 📋 Anforderungen & Checkliste

Um eine intuitive und sichere Suche zu gewährleisten, wurden folgende Anforderungen implementiert:
* [x] **Multifunktionale Suche**: Es ist möglich, sowohl nach öffentlichen als auch nach eigenen Notizen zu suchen.
* [x] **Dynamische Anzeige**: Der eingegebene Suchbegriff wird in Echtzeit über den Ergebnissen eingeblendet.
* [x] **URL-Synchronisation**: Der Suchbegriff wird als Query-Parameter in der URL geführt (z. B. `?q=suchbegriff`).
* [x] **Security-First**: Eingaben werden vor der Verarbeitung bereinigt, um Angriffe zu verhindern.

---

## 🛠 Technische Umsetzung

- **Komponente:** `SearchBar.vue`
- **Reaktive Bindung:** Die Komponente nutzt ein doppeltes `v-model` Bindungsmuster (`modelValue` für den Text und `filterValue` für den Status).
- **URL-Query-Parameter:** - Die Suche ist mit dem Vue Router synchronisiert. Beim Tippen wird die URL via `router.replace({ query: { q: searchTerm } })` aktualisiert.
    - Dies ermöglicht es Nutzern, Suchergebnisse zu verlinken oder die Seite neu zu laden, ohne den Filterkontext zu verlieren (Beispiel: `https://my-app.de/search?q=geheim`).
- **Input-Validierung:** Anstatt einer direkten Bindung wird ein `@input`-Handler (`handleInput`) verwendet. Dieser fängt jede Tastatureingabe ab und bereinigt sie sofort.
- **Zustandsanzeige:** Falls ein Suchbegriff aktiv ist, wird dieser unterhalb der Suchleiste dynamisch eingeblendet (z. B. *Ergebnisse für: "Mein Projekt"*).



---

## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **Reflected XSS** | Ein Angreifer könnte versuchen, bösartigen Code in das Suchfeld einzugeben, der dann in der UI ("Ergebnisse für...") wieder ausgegeben wird. |
| **Vorbeugung (Aktiv)** | **Echtzeit-Sanitizing:** Die Methode `handleInput` nutzt `DOMPurify.sanitize()`, um jede Eingabe sofort von potenziell gefährlichen HTML-Tags oder JavaScript-Event-Handlern zu reinigen. |
| **URL-Manipulation** | Ein Nutzer könnte versuchen, schädliche Skripte direkt über den Query-Parameter `?q=` in die URL einzuschleusen. |
| **Vorbeugung** | Beim Laden der Seite wird der Query-Parameter ebenfalls durch `DOMPurify` gefiltert, bevor er in den internen State übernommen wird. |



---

## 🔒 Datenschutz

- **Lokale Verarbeitung:** Die Filterung der Notizen findet clientseitig statt. Die Suchbegriffe dienen lediglich der Filterung der bereits autorisierten Daten im Arbeitsspeicher des Browsers.
- **Datensparsamkeit:** Es werden keine Suchhistorien oder Nutzer-Anfragen auf dem Server gespeichert. Die URL-Query dient lediglich der lokalen Navigation und dem User-Experience-Komfort.

---

## 💡 UX Details
- **Klares Design:** Die Suchleiste nutzt ein abgerundetes Design (`rounded-3xl`) und dezente Schatten, um sich visuell von den Inhaltskarten abzuheben.
- **Responsive Layout:** Auf Mobilgeräten stapeln sich Suchfeld und Filter-Dropdown automatisch vertikal (`flex-col`), während sie auf Desktops nebeneinander stehen (`md:flex-row`).
- **Persistenz:** Durch die URL-Parameter bleibt die Suche auch beim Navigieren im Browserverlauf erhalten.