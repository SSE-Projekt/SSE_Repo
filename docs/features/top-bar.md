# Feature: Navigation & Session-Management (TopBar)

Die `TopBar`-Komponente ist das zentrale Navigations-Element der Applikation. Sie ermöglicht den Wechsel zwischen den Hauptansichten und verwaltet den Zugriff auf das Benutzerprofil sowie die Logout-Funktion.

## 🛠 Technische Umsetzung

- **Komponente:** `TopBar.vue`
- **Reaktive Navigation:** Nutzt `router-link` in Kombination mit dynamischen CSS-Klassen, um die aktive Route (`route.path`) optisch hervorzuheben (Active-State).
- **Rollenbasierte Sichtbarkeit (RBAC):** Die Schaltfläche "My Notes" wird nur gerendert, wenn die `user_rolle` in den Metadaten des Nutzers den Wert `2` (Autor) aufweist.
- **Dropdown-Logik:** Ein benutzerdefiniertes Dropdown-Menü zeigt Benutzerdetails (Name und E-Mail) an.
- **Event-Handling:** Ein globaler Event-Listener (`handleClickOutside`) sorgt dafür, dass sich das Menü schließt, wenn der Nutzer außerhalb des Elements klickt.
- **Logout-Prozess:** Beim Abmelden wird der `user`-Eintrag aus dem `localStorage` entfernt und der Nutzer mittels `router.push('/login')` zurückgeführt.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **Insecure Logout** | Wenn nur die UI gewechselt wird, bleibt der Token gültig. **Vorbeugung:** `localStorage.removeItem('user')` zerstört die lokale Session-Information. (Empfehlung: Den JWT-Token zusätzlich im Backend entwerten). |
| **Bypass von UI-Elementen** | Ein Nutzer könnte versuchen, die URL `/my-notes` direkt aufzurufen, obwohl der Button versteckt ist. **Vorbeugung:** Die Zugriffskontrolle erfolgt nicht nur im Frontend via `v-if`, sondern muss durch *Route-Guards* im Router und Berechtigungsprüfungen im Backend abgesichert sein. |
| **Memory Leaks** | Event-Listener für das Dropdown könnten den Speicher belasten. **Vorbeugung:** Sauberes Entfernen des Click-Listeners im `onBeforeUnmount` Hook. |



## 🔒 Datenschutz

- **Datensparsamkeit:** Die TopBar zeigt nur den Nutzernamen und die E-Mail an, die bereits zur Authentifizierung notwendig waren. Es werden keine weiteren sensiblen Profilfaten im Frontend geladen.
- **Lokale Datenvernichtung:** Durch das Löschen des `user`-Objekts aus dem `localStorage` beim Logout wird sichergestellt, dass nachfolgende Nutzer am selben Gerät keine Informationen über den vorherigen Account (wie E-Mail-Adresse) einsehen können.

---

## 💡 UX Details
- **Sticky Header:** Durch `sticky top-0` bleibt die Navigation beim Scrollen immer erreichbar.
- **Visuelles Feedback:** Sanfte Transitionen (`transition-all`) beim Hovern über Navigations-Items verbessern das haptische Gefühl der Anwendung.
- **Profil-Avatar:** Ein minimalistisches Icon dient als intuitiver Einstiegspunkt für Kontoeinstellungen und Logout.