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

<!--      <div class="p-6 bg-gray-50 border-t border-gray-100 flex items-center justify-between text-xs text-gray-400">-->
<!--        <span>ID: {{ id }}</span>-->
<!--      </div>-->

      <Transition name="fade">
        <div v-if="showShareModal" class="absolute inset-0 z-20 bg-white/95 flex flex-col p-8 backdrop-blur-sm">
          <div class="flex justify-between items-center mb-8">
            <h2 class="text-xl font-bold text-gray-900">Notiz teilen</h2>
            <p class="text-[15px] font-mono text-black-500 bg-blue-50 px-2 py-1 rounded mt-1 break-all">
              {{ shareUrl }}
            </p>
            <button @click="showShareModal = false" class="p-2 hover:bg-gray-100 rounded-full">
              <svg viewBox="0 0 24 24" class="w-6 h-6 fill-current"><path :d="getIcon('close')" /></svg>
            </button>
          </div>

          <p class="text-sm text-gray-500 mb-4">Wähle einen Nutzer aus, um diese Notiz zu teilen:</p>

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
        Notiz erfolgreich geteilt!
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

// UI States
const showShareModal = ref(false);
const shareSuccess = ref(false);
const route = useRoute();
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

//generierung der URl zur Teilung
const shareUrl = computed(() => {
  return window.location.origin + window.location.pathname + '#/my-notes/' + props.id;
});

// Methods
const openShareModal = () => {
  showShareModal.value = true;
};

// const shareWith = (user) => {
//   console.log(`Note ${props.id} geteilt mit ${user.name}`);
//   showShareModal.value = false;
//   shareSuccess.value = true;
//   setTimeout(() => shareSuccess.value = false, 3000);
// };

const shareWith = (user) => {
  // URL
  const fullNoteLink = window.location.origin + window.location.pathname + '#/notes/' + props.id;

  // Vorbereitung der Object zu schicken
  const payload = {
    recipient: user.email,
    recipientName: user.name,
    message: `Hier est la note partagée : ${fullNoteLink}`,
    noteId: props.id,
    timestamp: new Date().toISOString()
  };

  // Simulation des Versands (Hier wird es in der Konsole angezeigt,
  // aber hier würde man eine API wie axios.post(‚/api/share‘, payload) aufrufen.
  console.log("--- ENVOI DU LIEN ---");
  console.log(`Vers : ${payload.recipientName} (${payload.recipient})`);
  console.log(`Contenu : ${payload.message}`);
  console.log("----------------------");

  // visual feedback
  showShareModal.value = false;
  shareSuccess.value = true;

  setTimeout(() => shareSuccess.value = false, 3000);
};
const renderedContent = computed(() => {
  if (!currentNote.value?.content) return '';

  const renderer = new marked.Renderer();

  renderer.image = ({ href, title, text }) => {
    const urlValue = typeof href === 'object' ? href.href : href;

    if (urlValue && urlValue.startsWith('embed:')) {
      const urlStr = urlValue.replace('embed:', '');

      try {
        let videoId = '';
        if (urlStr.includes('youtube.com/watch?v=')) {
          videoId = urlStr.split('v=')[1].split('&')[0];
        } else if (urlStr.includes('youtu.be/')) {
          videoId = urlStr.split('youtu.be/')[1].split('?')[0];
        } else if (urlStr.includes('youtube.com/embed/')) {
          videoId = urlStr.split('embed/')[1].split('?')[0];
        }

        if (videoId && /^[a-zA-Z0-9_-]+$/.test(videoId)) {
          return `
            <div class="video-container my-6 shadow-lg rounded-2xl overflow-hidden border border-gray-100"
                 style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
                <iframe
                  style="position: absolute; top: 0; left: 0; width: 100%; height: 100%;"
                  src="https://www.youtube-nocookie.com/embed/${videoId}"
                  title="${text || 'YouTube Video'}"
                  frameborder="0"
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                  allowfullscreen>
                </iframe>
            </div>`;
        }
      } catch (e) {
        console.error("Video ID extraction failed", e);
      }
    }

    return `<img src="${urlValue}" alt="${text || ''}" title="${title || ''}" class="rounded-xl mx-auto shadow-sm" />`;
  };

  const rawHtml = marked.parse(currentNote.value.content, { renderer: renderer });

  return DOMPurify.sanitize(rawHtml, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling", "loading", "style"],
  });
});
</script>

<style scoped>
.video-container {
  width: 100%;
  clear: both;
}

/* Animations transitions */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: scale(0.98);
}

.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.4s cubic-bezier(0.16, 1, 0.3, 1);
}
.slide-up-enter-from, .slide-up-leave-to {
  transform: translate(-50%, 100%);
  opacity: 0;
}
</style>