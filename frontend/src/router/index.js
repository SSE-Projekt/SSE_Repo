import { createRouter, createWebHashHistory } from 'vue-router'
import Anmeldung from '@/components/viewComponents/Anmeldung.vue'
import Registrierung from '@/components/viewComponents/Registrierung.vue'
import Start from '@/view/homeView.vue'
import MyNotes from '@/view/myNotesView.vue'
import NoteDetailView from '@/view/noteDetailView.vue'

const routes = [
    { path: '/', redirect: '/home' },
    { path: '/home', redirect: '/notes' },
    { path: '/login', component: Anmeldung },
    { path: '/register', component: Registrierung },
    { path: '/notes', component: Start },
    { path: '/my-notes', component: MyNotes },
    { path: '/notes/:id', component: NoteDetailView, props: true}
]

const router = createRouter({
    history: createWebHashHistory('#/my-app'), // Nutzt Hash-Modus für einfaches Deployment
    routes
})

export default router