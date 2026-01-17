# Feature: Suche und Filterung (SearchBar)

Die `SearchBar`-Komponente ist ein zentrales Steuerungselement, das es Nutzern ermöglicht, ihre Notizen-Sammlung effizient nach Stichworten zu durchsuchen und die Ansicht basierend auf der Sichtbarkeit (Privat vs. Öffentlich) zu filtern.

## 🛠 Technische Umsetzung

- **Komponente:** `SearchBar.vue`
- **Reaktive Bindung:** Die Komponente nutzt ein doppeltes `v-model` Bindungsmuster (`modelValue` für den Text und `filterValue` für den Status).
- **Input-Validierung:** Anstatt einer direkten Bindung wird ein `@input`-Handler (`handleInput`) verwendet. Dieser fängt jede Tastatureingabe ab und bereinigt sie sofort, bevor sie an die übergeordnete Komponente weitergegeben wird.
- **Zustandsanzeige:** Falls ein Suchbegriff aktiv ist, wird dieser unterhalb der Suchleiste dynamisch eingeblendet, um dem Nutzer den aktuellen Filterkontext zu bestätigen.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **Reflected XSS** | Ein Angreifer könnte versuchen, bösartigen Code in das Suchfeld einzugeben, der dann in der UI ("Ergebnisse für...") wieder ausgegeben wird. |
| **Vorbeugung (Aktiv)** | **Echtzeit-Sanitizing:** Die Methode `handleInput` nutzt `DOMPurify.sanitize()`, um jede Eingabe sofort von potenziell gefährlichen HTML-Tags oder JavaScript-Event-Handlern zu reinigen. |
| **SQL-/Log-Injection** | Manipulation der Suchanfrage, um Datenbanken oder Log-Dateien anzugreifen. |
| **Vorbeugung** | Durch die Bereinigung bereits im Frontend gelangen keine kritischen Steuerzeichen in die Datenverarbeitungskette. |



## 🔒 Datenschutz

- **Lokale Verarbeitung:** Die Filterung der Notizen findet clientseitig statt. Die Suchbegriffe dienen lediglich der Filterung der bereits autorisierten Daten im Arbeitsspeicher des Browsers.
- **Datensparsamkeit:** Es werden keine Suchhistorien oder Nutzer-Anfragen auf dem Server gespeichert, sofern dies nicht explizit für analytische Zwecke (hier nicht der Fall) vorgesehen ist.

---

## 💡 UX Details
- **Klares Design:** Die Suchleiste nutzt ein abgerundetes Design (`rounded-3xl`) und dezente Schatten, um sich visuell von den Inhaltskarten abzuheben.
- **Responsive Layout:** Auf Mobilgeräten stapeln sich Suchfeld und Filter-Dropdown automatisch vertikal (`flex-col`), während sie auf Desktops nebeneinander stehen (`md:flex-row`).