import { createRouter, createWebHashHistory } from 'vue-router'
import Anmeldung from '@/view/Anmeldung.vue'
import Search from '@/components/viewComponents/SearchBar.vue'
import Registrierung from '@/view/Registrierung.vue'
import Start from '@/view/homeView.vue'
import MyNotes from '@/view/myNotesView.vue'
import shareNotes from '@/view/shareNoteView.vue'
import NoteDetailView from '@/view/noteDetailView.vue'
import Forbidden from '@/view/ForbiddenView.vue'

const routes = [
    { path: '/', redirect: '/home' },
    { path: '/home', redirect: '/notes' },
    { path: '/search', component: Search },
    { path: '/login', component: Anmeldung },
    { path: '/register', component: Registrierung },
    { path: '/notes', component: Start },
    {
        path: '/my-notes',
        component: MyNotes,
        //   ERFORDERLICHE METADATEN HINZUFÜGEN
        meta: { requiresRole: 'autor' }
    },
    { path: '/share-notes', component: shareNotes },
    { path: '/notes/:id', component: NoteDetailView, props: true},
    { path: '/403', component: Forbidden, name: 'forbidden' },
    // „Catch-all”-Route für nicht vorhandene Seiten
    { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
    history: createWebHashHistory('#/my-app'), // Nutzt Hash-Modus für einfaches Deployment
    routes
})
//DER NAVIGATION GUARD
router.beforeEach((to, from, next) => {
    // Wir rufen die gespeicherte Rolle ab (über localStorage oder deinen globalen Status).
    // Für den Test simulieren wir den Abruf der Rolle:
    const user = JSON.parse(localStorage.getItem('user') || '{"role": "autor"}');
    const userRole = user.role;

    // Wenn die Route die Rolle „autor” erfordert und der Benutzer nicht „autor” ist
    if (to.meta.requiresRole === 'autor' && userRole !== 'autor') {
        next({ name: 'forbidden' });
    } else {
        next(); // Erlaubt die Navigation
    }
})

export default router