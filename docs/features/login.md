# Feature: Login (Anmeldung)

Der Login-Prozess ist das Tor zur Applikation und stellt sicher, dass nur authentifizierte Nutzer Zugriff auf ihre privaten Notizen erhalten.

## 🛠 Technische Umsetzung

- **Frontend:** Die Komponente `Anmeldung.vue` verwaltet den Status der Eingabefelder `username` und `password`.
- **Authentifizierung:** Beim Absenden wird eine `POST`-Anfrage an den `/api/auth/login` Endpunkt gesendet.
- **Session-Management:** - Bei Erfolg liefert das Backend einen **JWT (JSON Web Token)** sowie das Nutzerprofil zurück.
    - Der Token wird im `localStorage` gespeichert und dient als Autorisierung für alle zukünftigen API-Anfragen.
- **UX-Elemente:** Ein Lade-Spinner (`isLoading`) verhindert Mehrfacheingaben während der Serverantwort.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS Attacken** | Der Nutzername wird mittels `DOMPurify` bereinigt, um Skript-Injektionen im Login-Log zu verhindern. |
| **Brute-Force Angriffe** | Die Schaltfläche wird nach dem Klicken deaktiviert. (Empfehlung: Serverseitiges Rate-Limiting). |
| **Session-Fixierung** | Der Token wird lokal sicher gespeichert und beim Logout durch `localStorage.clear()` sofort vernichtet. |

## 🔒 Datenschutz

- **HTTPS:** Die Übertragung der Anmeldedaten erfolgt ausschließlich über verschlüsselte Verbindungen.
- **Minimale Datenspeicherung:** Im Browser werden nur die für die Session notwendigen Metadaten (Username, Rolle) zwischengespeichert.