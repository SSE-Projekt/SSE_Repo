import { createRouter, createWebHashHistory } from 'vue-router'
import Anmeldung from '@/view/Anmeldung.vue'
import Search from '@/components/viewComponents/SearchBar.vue'
import Registrierung from '@/view/Registrierung.vue'
import Start from '@/view/homeView.vue'
import MyNotes from '@/view/myNotesView.vue'
import shareNotes from '@/view/shareNoteView.vue'
import NoteDetailView from '@/view/noteDetailView.vue'
import Forbidden from '@/view/ForbiddenView.vue'
import EditNoteView from '@/components/viewComponents/EditNoteView.vue';

const routes = [
    { path: '/', redirect: '/login' },
    { path: '/search', component: Search },
    { path: '/login', component: Anmeldung },
    { path: '/register', component: Registrierung },
    { path: '/home', component: Start },
    {
        path: '/my-notes',
        component: MyNotes,
        //   ERFORDERLICHE METADATEN HINZUFÜGEN
        meta: { requiresRole: 'autor' }
    },
    {
        path: '/edit/:id',
        name: 'edit-note',
        component: EditNoteView,
        props: true,
        meta: { requiresRole: 'autor' }
    },
    {
        path: '/reset-password',
        name: 'ResetPassword',
        component: () => import('@/view/ResetPasswordView.vue')
    },
    { path: '/share-notes', component: shareNotes },
    { path: '/notes/:id', component: NoteDetailView, props: true},
    { path: '/403', component: Forbidden, name: 'forbidden' },
    // „Catch-all”-Route für nicht vorhandene Seiten
    { path: '/:pathMatch(.*)*', redirect: '/login' }
]

const router = createRouter({
    history: createWebHashHistory('#/my-app'), // Nutzt Hash-Modus für einfaches Deployment
    routes
})
//DER NAVIGATION GUARD
router.beforeEach((to, from, next) => {
    const publicPages = ['/reset-password', '/login', '/register'];
    const authRequired = !publicPages.includes(to.path);

    const user = JSON.parse(localStorage.getItem('user'))?? null

    // 1. SI PAS CONNECTÉ et tente d'aller ailleurs que Login/Register
    if (authRequired && !user) {
        // On force l'arrêt sur login
        return next('/login');
    }

    if (to.query.type === 'recovery') {
        next('/reset-password')
    } else {
        next()
    }

    if (next.query.type === 'recovery') {
        next('/reset-password')
    } else {
        next()
    }

    if (from.query.type === 'recovery') {
        next('/reset-password')
    } else {
        next()
    }

    // 2. SI DÉJÀ CONNECTÉ et tente d'aller sur Login
    if (user && (to.path === '/login' || to.path === '/register')) {
        return next('/home');
    }

    // 3. VÉRIFICATION DES RÔLES
    if (to.meta.requiresRole === 'autor' && user?.user_metadata.user_rolle !== 2) {
        return next({ name: 'forbidden' });
    }

    // Sinon, on laisse passer
    next();
});

export default router