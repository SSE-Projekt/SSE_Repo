<template>
  <div class="min-h-screen bg-[#fafafa] py-12 px-4">
    <div v-if="currentNote" class="max-w-3xl mx-auto bg-white border border-gray-200 rounded-3xl shadow-sm overflow-hidden relative">

      <div class="p-8 border-b border-gray-50 bg-gray-50/50">
        <div class="flex items-center justify-between mb-6">
          <button @click="router.back()" class="text-sm text-gray-500 hover:text-black flex items-center gap-2 transition-colors">
            <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current"><path :d="getIcon('arrow-left')" /></svg>
            Zurück
          </button>

          <div class="flex items-center gap-2">
            <button
                v-if="route.query.from === 'my-notes'"
                @click="openShareModal"
                class="flex items-center gap-2 px-3 py-1 bg-white border border-gray-200 rounded-full text-xs font-medium text-gray-600 hover:border-black hover:text-black transition-all"
            >
              <svg viewBox="0 0 24 24" class="w-3.5 h-3.5 fill-current"><path :d="getIcon('share-variant')" /></svg>
              Teilen
            </button>

            <div class="flex items-center gap-2 px-3 py-1 bg-white border rounded-full text-xs font-medium text-gray-500">
              <svg viewBox="0 0 24 24" class="w-3 h-3 fill-current">
                <path :d="currentNote.isPrivate ? getIcon('lock') : getIcon('earth')" />
              </svg>
              {{ currentNote.isPrivate ? 'Privater Link' : 'Öffentlich' }}
            </div>
          </div>
        </div>

        <h1 class="text-3xl font-bold text-gray-900 mb-4">Notes Details</h1>

        <div class="flex items-center gap-6 text-sm text-gray-500">
          <div class="flex items-center gap-2">
            <div class="w-6 h-6 bg-gray-200 rounded-full flex items-center justify-center">
              <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current"><path :d="getIcon('account')" /></svg>
            </div>
            <span>Erstellt von <span class="font-semibold text-gray-700">{{ currentNote.author || 'User' }}</span></span>
          </div>
          <div class="flex items-center gap-2">
            <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current"><path :d="getIcon('clock-outline')" /></svg>
            <span>{{ currentNote.date }}</span>
          </div>
        </div>
      </div>

      <div class="p-8 prose prose-slate max-w-none min-h-[300px]" v-html="renderedContent"></div>

      <div class="p-6 bg-gray-50 border-t border-gray-100 flex items-center justify-between">
        <span class="text-xs text-gray-400 font-mono">ID: {{ id }}</span>

        <div class="flex items-center gap-3">
          <button
              v-if="route.query.from === 'my-notes'"
              @click="editNote"
              class="flex items-center gap-2 px-4 py-2 bg-white border border-gray-200 rounded-xl text-sm font-medium text-gray-700 hover:border-black hover:text-black transition-all shadow-sm"
          >
            <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current"><path :d="getIcon('pencil')" /></svg>
            Bearbeiten
          </button>
          <button
              v-if="route.query.from === 'my-notes'"
              @click="deleteNote"
              class="flex items-center gap-2 px-4 py-2 bg-rose-50 border border-rose-100 rounded-xl text-sm font-medium text-rose-600 hover:bg-rose-600 hover:text-white transition-all shadow-sm"
          >
            <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current"><path :d="getIcon('delete')" /></svg>
            Löschen
          </button>
        </div>
      </div>

      <Transition name="fade">
        <div v-if="showShareModal" class="absolute inset-0 z-20 bg-white/95 flex flex-col p-8 backdrop-blur-sm">
          <div class="flex justify-between items-center mb-4">
            <div>
              <h2 class="text-xl font-bold text-gray-900">Notiz teilen</h2>
              <p class="text-[11px] font-mono text-blue-600 bg-blue-50 px-2 py-1 rounded mt-1 break-all">
                {{ shareUrl }}
              </p>
            </div>
            <button @click="showShareModal = false" class="p-2 hover:bg-gray-100 rounded-full">
              <svg viewBox="0 0 24 24" class="w-6 h-6 fill-current"><path :d="getIcon('close')" /></svg>
            </button>
          </div>

          <p class="text-sm text-gray-500 mb-4">Wähle einen Nutzer aus, um cette note zu teilen:</p>

          <div class="flex-1 overflow-y-auto space-y-2">
            <div
                v-for="user in otherUsers"
                :key="user.id"
                @click="shareWith(user)"
                class="flex items-center justify-between p-4 border border-gray-100 rounded-2xl hover:border-black hover:bg-gray-50 cursor-pointer transition-all group"
            >
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center font-bold text-gray-600">
                  {{ user.name.charAt(0) }}
                </div>
                <div>
                  <div class="font-semibold text-gray-800">{{ user.name }}</div>
                  <div class="text-xs text-gray-400">{{ user.email }}</div>
                </div>
              </div>
              <svg viewBox="0 0 24 24" class="w-5 h-5 text-gray-300 group-hover:text-black fill-current">
                <path :d="getIcon('plus')" />
              </svg>
            </div>
          </div>
        </div>
      </Transition>
    </div>

    <div v-else class="text-center py-20">
      <h2 class="text-xl font-semibold text-gray-600">Notiz nicht gefunden.</h2>
      <router-link to="/home" class="text-blue-500 hover:underline mt-4 inline-block">Zurück zum Dashboard</router-link>
    </div>

    <Transition name="slide-up">
      <div v-if="shareSuccess" class="fixed bottom-10 left-1/2 -translate-x-1/2 bg-black text-white px-6 py-3 rounded-full shadow-2xl text-sm z-50">
        Aktion erfolgreich!
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { marked } from 'marked';
import { getIcon } from '@/utils/getIcon';
import DOMPurify from 'dompurify';

const props = defineProps({
  id: String
});

const router = useRouter();
const route = useRoute();

// UI States
const showShareModal = ref(false);
const shareSuccess = ref(false);

// Simulation Auth/Users
const currentUserEmail = "mon-email@exemple.com";
const allUsers = ref([
  { id: 1, name: "Max Müller", email: "max@test.de" },
  { id: 2, name: "Anna Schmidt", email: "anna@test.de" },
  { id: 3, name: "Kevin Admin", email: "kevin@admin.de" },
  { id: 4, name: "Ton Email", email: "mon-email@exemple.com" }
]);

const otherUsers = computed(() => allUsers.value.filter(u => u.email !== currentUserEmail));

// Data Fetching (LocalStorage)
const notesStore = JSON.parse(localStorage.getItem('notes') || '[]');
const currentNote = computed(() => notesStore.find(n => n.id === props.id));

// URL zur Teilung
const shareUrl = computed(() => {
  return window.location.origin + window.location.pathname + '#/notes/' + props.id;
});

// --- ACTIONS ---

const openShareModal = () => {
  showShareModal.value = true;
};

const shareWith = (user) => {
  const fullNoteLink = shareUrl.value;

  console.log("--- ENVOI DU LIEN ---");
  console.log(`Vers : ${user.name} (${user.email})`);
  console.log(`Lien : ${fullNoteLink}`);

  showShareModal.value = false;
  shareSuccess.value = true;
  setTimeout(() => shareSuccess.value = false, 3000);
};

const editNote = () => {
  router.push(`/edit/${props.id}`);
};

const deleteNote = () => {
  if (confirm('Möchtest du diese Notiz wirklich löschen?')) {
    const allNotes = JSON.parse(localStorage.getItem('notes') || '[]');
    const updatedNotes = allNotes.filter(n => n.id !== props.id);
    localStorage.setItem('notes', JSON.stringify(updatedNotes));

    shareSuccess.value = true;
    setTimeout(() => {
      shareSuccess.value = false;
      router.push('/my-notes');
    }, 1000);
  }
};

// --- RENDER LOGIC (Markdown + Video + Security) ---

const renderedContent = computed(() => {
  if (!currentNote.value?.content) return '';

  const renderer = new marked.Renderer();

  renderer.image = ({ href, title, text }) => {
    const urlValue = typeof href === 'object' ? href.href : href;

    if (urlValue && urlValue.startsWith('embed:')) {
      const urlStr = urlValue.replace('embed:', '');
      try {
        let videoId = '';
        if (urlStr.includes('v=')) videoId = urlStr.split('v=')[1].split('&')[0];
        else if (urlStr.includes('youtu.be/')) videoId = urlStr.split('youtu.be/')[1].split('?')[0];
        else if (urlStr.includes('embed/')) videoId = urlStr.split('embed/')[1].split('?')[0];

        if (videoId && /^[a-zA-Z0-9_-]+$/.test(videoId)) {
          return `
            <div class="video-container my-6 shadow-lg rounded-2xl overflow-hidden" style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
                <iframe style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"
                  src="https://www.youtube-nocookie.com/embed/${videoId}" frameborder="0" allowfullscreen></iframe>
            </div>`;
        }
      } catch (e) { console.error(e); }
    }
    return `<img src="${urlValue}" alt="${text || ''}" class="rounded-xl mx-auto shadow-sm" />`;
  };

  const rawHtml = marked.parse(currentNote.value.content, { renderer: renderer });

  return DOMPurify.sanitize(rawHtml, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling", "loading", "style"],
  });
});
</script>

<style scoped>
.video-container { width: 100%; clear: both; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease, transform 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; transform: scale(0.98); }

.slide-up-enter-active, .slide-up-leave-active { transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1); }
.slide-up-enter-from, .slide-up-leave-to { transform: translate(-50%, 100%); opacity: 0; }
</style>