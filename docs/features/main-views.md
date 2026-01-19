# Hauptansichten & Rollenkonzept (Home, My Notes, Shared)

Dieses Dokument beschreibt die Architektur der Hauptansichten der Applikation und wie der Zugriff basierend auf Benutzerrollen und Berechtigungen gesteuert wird.

## 👥 Rollenbasierte Zugriffskontrolle (RBAC)

Die Anwendung unterscheidet strikt zwischen zwei Nutzertypen, um die Integrität der Daten zu gewährleisten:
Diese Rollen werden im JWT-Token mitgeführt und sowohl im Vue-Frontend als auch in den **Supabase RLS Policies** geprüft.

| Rolle | ID | Berechtigungen | Einschränkungen                                                        |
| :--- | :--- | :--- |:-----------------------------------------------------------------------|
| **Leser** | 1 | Kann öffentliche Notizen suchen und lesen. | Kann keine eigenen Notizen erstellen, bearbeiten, teilen oder löschen. |
| **Autor** | 2 | Vollzugriff: Erstellen, Bearbeiten, Löschen und Verwalten privater Notizen. | Keine (innerhalb der eigenen Datenhoheit).                             |

---

## 🏠 Übersicht der Ansichten

### 1. Home & My Notes (`MyNotes.vue`)
Dies ist das persönliche Dashboard für Nutzer mit der Rolle **Autor**.
- **Technische Umsetzung:** Die Seite lädt Notizen aus dem `Supabase` (oder der API) und filtert diese primär nach der Zugehörigkeit zum aktuellen Nutzer.
- **Interaktion:** Enthält die `entry-card` Komponente, die als Schnittstelle für die Erstellung neuer Inhalte dient.
- **Navigation:** Nutzt URL-Parameter (`?from=my-notes`), um den Kontext beim Öffnen einer Notiz-Detailseite beizubehalten.

### 2. Shared Notes (`SharedNotes.vue`)
Diese Ansicht dient dem gemeinschaftlichen Austausch von Informationen.
- **Filterlogik:** Ein `computed property` filtert die Liste der Notizen so, dass nur Einträge mit dem Status `isPrivate: false` angezeigt werden.
- **Zweck:** Ermöglicht es Lesern und Autoren gleichermaßen, auf öffentliches Wissen zuzugreifen, ohne die Privatsphäre anderer Nutzer zu verletzen.



---

## 🛠 Technische Kernfunktionen

### URL-Synchronisation & Suche
Beide Ansichten sind mit der `SearchBar` verbunden. Jede Änderung der Suche oder des Filters wird sofort in der URL gespiegelt:
- **Mechanismus:** `router.replace` aktualisiert die Query-Parameter `q` (Suche) und `type` (Filter).
- **Vorteil:** Dies ermöglicht "Deep Linking". Ein Nutzer kann einen spezifischen Suchzustand kopieren und teilen.

### Dynamische Filterung
Die Anzeige der Notizen erfolgt über eine reaktive Filterkette:
1. **Suche:** Textvergleich zwischen `searchQuery` und den Titeln/Inhalten der Notizen.
2. **Status-Filter:** Selektion nach `all`, `private` oder `public`.

## 🐳 Deployment & Docker
Die gesamte Anwendung (Frontend & Backend) ist **Docker-ready**.
- **Portabilität:** Die App läuft identisch auf jedem System.
- **Sicherheit:** Docker-Container isolieren die Applikationslogik vom Host-System.
---

## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **Rollen-Bypass (Frontend)** | Ein Nutzer mit der ID 1 (Leser) könnte versuchen, die `MyNotes`-Route manuell aufzurufen. |
| **Vorbeugung** | **Route-Guards:** Der Vue-Router prüft vor dem Laden der Seite die `user_metadata.user_rolle`. Zusätzlich blendet die `TopBar` Navigationslink für unberechtigte Rollen aus. |
| **Information Disclosure** | Private Notizen könnten fälschlicherweise in der öffentlichen Liste erscheinen. |
| **Vorbeugung** | **Strikte Filterung:** Die Filterlogik im Frontend nutzt explizite Booleans (`note.isPrivate === true`). Im produktiven Backend erfolgt die Filterung bereits auf Datenbankebene (SQL-Scope). |
| **XSS via Suchanfrage** | Anzeige des Suchbegriffs im UI ("Ergebnisse für..."). |
| **Vorbeugung** | Vue.js nutzt automatisches HTML-Escaping bei der Text-Interpolation (`{{ }}`). |



---

## 🔒 Datenschutz & Sicherheit

- **Privatsphäre per Default:** Neue Notizen können explizit als "Privat" markiert werden, wodurch sie für alle anderen Nutzer unsichtbar bleiben.
- **Eindeutige Identifizierung:** Die Applikation nutzt die im `localStorage` hinterlegten Nutzerdaten (`user_metadata`), um sicherzustellen, dass Autoren nur ihre eigenen Notizen modifizieren können.
- **Feedback-System:** Über die `SnackBar` wird der Nutzer über den Erfolg von Aktionen oder über blockierte Sicherheitsrisiken (z.B. ungültige Eingaben) informiert.

---

## 💡 UX Besonderheiten
- **Empty States:** Wenn keine Notizen den Filterkriterien entsprechen, erscheint ein visueller Platzhalter ("No notes yet"), um Verwirrung zu vermeiden.
- **Responsive Design:** Die Layouts für Home und Shared Notes nutzen ein einspaltiges Design auf Mobilgeräten und optimierte Abstände auf Desktops (`max-w-4xl`).