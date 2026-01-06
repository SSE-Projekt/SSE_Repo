## Lokale Supabase starten

## Secure Notes Backend

Dieses Spring Boot Backend verwaltet die Authentifizierung für die Secure Notes App. Es nutzt **Supabase Auth** als Identitätsanbieter und erweitert diesen um eine benutzerdefinierte Username-Logik.

### Features

* **Automatische Username-Generierung**: Erstellt Usernames basierend auf den ersten drei Buchstaben der E-Mail (z. B. `ncb1`, `ncb2`).
* **Supabase Integration**: Registrierung und Login werden sicher an die Supabase Auth API weitergeleitet.
* **Flexibler Login**: Nutzer können sich wahlweise mit ihrer **E-Mail** oder ihrem **Usernamen** anmelden.
* **E-Mail Benachrichtigung**: Automatischer Versand einer Willkommens-E-Mail via SMTP (Mailpit).
* **JWT Security**: Absicherung der Endpunkte mittels Spring Security und JWT-Validierung.

### Voraussetzungen

* **Java 17+**
* **Maven**
* **Docker Desktop** (für Supabase & Mailpit)
* **Supabase CLI**

### API Endpunkte

#### Authentifizierung (`/api/auth`)

| Methode | Endpunkt | Beschreibung |
| --- | --- | --- |
| `POST` | `/signup` | Registriert User, generiert Username & sendet Mail. |
| `POST` | `/login` | Login via `login` (Email/Username) & `password`. |

### Installation & Start

1. **Supabase starten:**

```bash
npm install
npx supabase start
```

2. **Backend starten:**
```bash
mvn spring-boot:run

```


3. **Mail-Interface aufrufen:**
   Öffne `http://localhost:54324`, um gesendete E-Mails in Mailpit zu sehen.


Ich habe das README um einen Abschnitt ergänzt, der genau erklärt, wie du die API-Endpunkte mit gängigen Tools wie **cURL**, **Postman** oder **HTTPie** testen kannst.

Hier ist der neue Abschnitt für dein `README.md`:

---

### API Testing Guide

Du kannst die Routen mit jedem HTTP-Client testen. Hier sind die Beispiele für die wichtigsten Befehle:

### 1. Registrierung (`/signup`)

Dieser Endpunkt generiert den Usernamen, speichert ihn in Supabase und sendet eine E-Mail.

**Teste mit Body:**

```bash
     {
       "email": "testuser@example.com",
       "password": "sichererPasswort123"
     }'
```

**Erwartete Antwort (200 OK):** Ein JSON-Objekt von Supabase, das den `user_name` in den `user_metadata` enthält.

---

### 2. Login (`/login`)

Du kannst dich entweder mit deiner **E-Mail** oder deinem generierten **Usernamen** einloggen.

**Login mit Username:**

```bash
     {
       "login": "tes1",
       "password": "sichererPasswort123"
     }'

```

Dann gehe auf `http://localhost:54324`, um zu prüfen, ob die Willkommens-Mail für den neuen User angekommen ist.

**Erwartete Antwort (200 OK):** ```json
{
"access_token": "ey...",
"token_type": "bearer",
"user": { ... }
}

---

### 🛠 Tools zum Testen

* **Postman / Insomnia:** Erstelle eine neue POST-Anfrage, setze den Body auf `raw` -> `JSON` und füge die Daten ein.
* **Mailpit UI:** Gehe auf `http://localhost:54324`, um zu prüfen, ob die Willkommens-Mail für den neuen User angekommen ist.
* **IntelliJ HTTP Client:** Erstelle eine `.http` Datei in deinem Projekt und schreibe die Requests direkt dort hinein.

---

## Lizenz

Dieses Projekt ist für private Lernzwecke gedacht.

