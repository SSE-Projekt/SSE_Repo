# Infrastruktur & Deployment (Docker)

Die Architektur von **Secure Notes** ist vollständig containerisiert. Dies garantiert eine konsistente Laufzeitumgebung und eine strikte Trennung der Dienste zwischen Frontend, Backend, E-Mail-Service und der Datenbank.

## 🛠 Technische Umsetzung (Docker Compose)

Die Orchestrierung erfolgt über **Docker Compose**, wobei drei Hauptdienste in einem isolierten Netzwerk zusammenarbeiten.



### 1. Backend Service (`sse_backend_app`)
* **Technologie**: **Spring Boot** mit **Maven**.
* **Port**: Erreichbar über Port `8080`.
* **Datenbank-Anbindung**: Die Verbindung erfolgt über JDBC an die Supabase-PostgreSQL-Instanz innerhalb des Docker-Netzwerks.
* **Sicherheit**: Sensible Anmeldedaten für die Datenbank werden als Umgebungsvariablen (`SPRING_DATASOURCE`) direkt im Container verwaltet und nicht im Quellcode exponiert.

### 2. Frontend Service (`sse_frontend_app`)
* **Technologie**: **Vue.js 3**.
* **Port**: Erreichbar über Port `80` (Standard HTTP).
* **Verantwortung**: Dient als Einstiegspunkt für den Nutzer und kommuniziert mit dem Backend-Container über das interne Netzwerk.

### 3. Mail-Testing Service (`sse_mailpit`)
* **Image**: Nutzt das `axllent/mailpit` Image.
* **Ports**: `8025` für das Web-Interface und `1025` für den SMTP-Versand.
* **Zweck**: Fängt alle ausgehenden E-Mails (z. B. Passwort-Reset) lokal ab, um die Sicherheit und den Datenschutz während der Entwicklung zu gewährleisten, ohne echte E-Mails versenden zu müssen.

---

## 🌐 Netzwerk-Architektur & Sicherheit

Das System nutzt ein externes Netzwerk, um eine Brücke zur **Supabase-Infrastruktur** zu schlagen.

* **Externes Netzwerk**: Die Anbindung erfolgt über das bestehende `supabase_network_SSE_Repo`.
* **Isolation**: Da das Netzwerk als `external` markiert ist, können die Container sicher auf die Datenbank-Instanz zugreifen, ohne den Datenbank-Port (`5432`) nach außen für das öffentliche Internet öffnen zu müssen.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **Exposure von DB-Credentials** | **Implementiert**: Passwörter stehen nicht im Quellcode (Hardcoded), sondern werden erst beim Container-Start über die Docker-Umgebung injiziert. |
| **Man-in-the-Middle (Intern)** | **Implementiert**: Die Kommunikation zwischen Backend und Datenbank findet im privaten Docker-Netzwerk statt, das von externen Zugriffen isoliert ist. |
| **Unbefugter Zugriff (DB)** | **RLS & Auth**: Selbst bei Netzwerkzugriff schützt die **Row Level Security** (RLS) in der Datenbank die Zeilen vor unbefugtem Zugriff durch falsche Identitäten. |

## 🔒 Datenschutz

* **E-Mail-Privatsphäre**: Durch den Einsatz von **Mailpit** verlassen keine Test-E-Mails die lokale Umgebung. Es werden keine echten SMTP-Relays genutzt, was den Abfluss von Nutzerdaten während der Testphase verhindert.
* **Daten-Souveränität**: Durch die Containerisierung ist die Applikation portabel und kann auf eigenen Servern betrieben werden, was die volle Kontrolle über die Datenhaltung ermöglicht.

---

## 💡 Deployment-Befehle

Um die gesamte Umgebung zu starten:

```bash
# Starten der Infrastruktur im Hintergrund
docker-compose up -d

# Status der Container prüfen
docker ps