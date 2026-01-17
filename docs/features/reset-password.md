# Feature: Passwort zurücksetzen

### 🛠 Technische Umsetzung
- **Frontend:** Vue.js Komponente `ResetPasswordView.vue`.
- **Prozess:** Der User erhält einen Link mit einem `token` und seiner `email` als URL-Parameter.
- **Validierung:** `onMounted` Hook extrahiert die Parameter. Ein `computed` Property prüft die Übereinstimmung der beiden Passwort-Felder.

### 🛡 Schwachstellen & Vorbeugung
| Schwachstelle | Vorbeugung |
| :--- | :--- |
| **XSS Attacken** | Einsatz von `DOMPurify` vor dem API-Call. |
| **Injection** | Strikte Regex-Prüfung verhindert Symbole wie `< > ' " ( ) -`. |
| **Token-Hijacking** | Die API entwertet den Token sofort nach einmaliger Nutzung. |

### 🔒 Datenschutz
- Es werden keine Passwörter im Klartext übertragen (HTTPS).
- Der Sicherheits-Token in der URL ist zeitlich begrenzt und enthält keine sensiblen Nutzerdaten.