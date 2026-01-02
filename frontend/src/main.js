import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/main.css'
//Ziel: Verwendung in allen Komponenten ohne erneuten Import.
import DOMPurify from 'dompurify'

//Vorteile
//Nur eine Konfiguration erforderlich
//Überall wiederverwendbar
//Zentralisiert (einfachere Sicherheitsüberprüfung)
//Ideal für mittlere/große Projekte
const app = createApp(App)
app.use(router) // Hier wird der Router aktiviert
app.config.globalProperties.$sanitize = (dirty) => {
    return DOMPurify.sanitize(dirty)
}//  DOMPurify global machen
app.mount('#app')