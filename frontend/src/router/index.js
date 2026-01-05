import { createRouter, createWebHashHistory } from 'vue-router'
import Anmeldung from '@/view/Anmeldung.vue'
import Search from '@/components/viewComponents/SearchBar.vue'
import Registrierung from '@/view/Registrierung.vue'
import Start from '@/view/homeView.vue'
import MyNotes from '@/view/myNotesView.vue'
import shareNotes from '@/view/shareNoteView.vue'
import NoteDetailView from '@/view/noteDetailView.vue'

const routes = [
    { path: '/', redirect: '/home' },
    { path: '/home', redirect: '/notes' },
    { path: '/search', component: Search },
    { path: '/login', component: Anmeldung },
    { path: '/register', component: Registrierung },
    { path: '/notes', component: Start },
    { path: '/my-notes', component: MyNotes },
    { path: '/share-notes', component: shareNotes },
    { path: '/notes/:id', component: NoteDetailView, props: true}
]

const router = createRouter({
    history: createWebHashHistory('#/my-app'), // Nutzt Hash-Modus für einfaches Deployment
    routes
})

export default router