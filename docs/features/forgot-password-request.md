# Feature: Passwort vergessen (Anfrage)

Diese Funktion ermöglicht es Nutzern, die ihr Passwort vergessen haben, einen sicheren Wiederherstellungs-Link per E-Mail anzufordern.

## 🛠 Technische Umsetzung

- **Komponente:** `ForgotPasswordModal.vue`
- **Typ:** Wiederverwendbare Modal-Komponente, die über ein `v-model` gesteuert wird.
- **Logik:** - Die Komponente kann mit einer `initialEmail` (vorausgefüllt aus dem Login-Feld) initialisiert werden.
    - Beim Absenden wird eine `POST`-Anfrage an `/api/auth/forgot-password` gesendet.
- **Event-Kommunikation:** Die Modal-Komponente nutzt `emits` (`success`, `error`, `warn`), um die übergeordnete Seite (z.B. Login) über den Status zu informieren, damit dort die globale SnackBar eingeblendet werden kann.



## 🛡️ Schwachstellen & Vorbeugung

| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS (Cross-Site Scripting)** | Bevor die E-Mail-Adresse an die API gesendet wird, erfolgt eine Bereinigung durch `DOMPurify`. Wenn manipulierte Zeichen erkannt werden, wird die Anfrage blockiert. |
| **User Enumeration** | (Backend-Empfehlung) Die Anwendung sollte idealerweise immer eine Erfolgsmeldung anzeigen, unabhängig davon, ob die E-Mail existiert, um das Ausspähen von Nutzerkonten zu verhindern. |
| **Spam / Mail-Flooding** | Durch den `isLoading` Status wird der Button während der Anfrage deaktiviert, um Mehrfachabsendungen zu verhindern. |



## 🔒 Datenschutz

- **Datenminimierung:** Es wird ausschließlich die E-Mail-Adresse abgefragt.
- **Transparenz:** Der Nutzer wird darüber informiert, dass er einen Link erhält, falls das Konto existiert.
- **Sichere Übertragung:** Die Kommunikation zwischen Frontend und Backend erfolgt über HTTPS, um das Abfangen der E-Mail-Adresse im Netzwerk zu verhindern.

---

## 💡 UX & UI Details
- **Backdrop-Blur:** Die Verwendung von `backdrop-blur-sm` auf dem Hintergrund-Overlay lenkt den Fokus des Nutzers voll auf die Modal-Interaktion.
- **Transitions:** Eine sanfte `fade`-Transition sorgt für ein modernes und flüssiges Interface-Gefühl beim Öffnen und Schließen.