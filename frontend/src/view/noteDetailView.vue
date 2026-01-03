<template>
  <div class="min-h-screen bg-[#fafafa] py-12 px-4">
    <div v-if="currentNote" class="max-w-3xl mx-auto bg-white border border-gray-200 rounded-3xl shadow-sm overflow-hidden">

      <div class="p-8 border-b border-gray-50 bg-gray-50/50">
        <div class="flex items-center justify-between mb-6">
          <button @click="router.back()" class="text-sm text-gray-500 hover:text-black flex items-center gap-2">
            <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current"><path :d="getIcon('arrow-left')" /></svg>
            Zurück
          </button>
          <div class="flex items-center gap-2 px-3 py-1 bg-white border rounded-full text-xs font-medium text-gray-500">
            <svg viewBox="0 0 24 24" class="w-3 h-3 fill-current">
              <path :d="currentNote.isPrivate ? getIcon('lock') : getIcon('earth')" />
            </svg>
            {{ currentNote.isPrivate ? 'Privater Link' : 'Öffentlich' }}
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

      <div class="p-6 bg-gray-50 border-t border-gray-100 flex items-center justify-between text-xs text-gray-400">
        <span>ID: {{ id }}</span>
      </div>
    </div>

    <div v-else class="text-center py-20">
      <h2 class="text-xl font-semibold text-gray-600">Notiz nicht gefunden.</h2>
      <router-link to="/home" class="text-blue-500 hover:underline mt-4 inline-block">Zurück zum Dashboard</router-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { marked } from 'marked';
import { getIcon } from '@/utils/getIcon';
import DOMPurify from 'dompurify';

const props = defineProps({
  id: String
});

const router = useRouter();

const notesStore = JSON.parse(localStorage.getItem('notes') || '[]');
const currentNote = computed(() => notesStore.find(n => n.id === props.id));

const renderedContent = computed(() => {
  if (!currentNote.value?.content) return '';

  const renderer = new marked.Renderer();

  // Wir nutzen die modernere Methode, um den Renderer anzupassen
  renderer.image = ({ href, title, text }) => {
    // Falls href ein Objekt ist (manche marked Versionen), extrahieren wir den String
    const urlValue = typeof href === 'object' ? href.href : href;

    if (urlValue && urlValue.startsWith('embed:')) {
      const urlStr = urlValue.replace('embed:', '');

      try {
        let videoId = '';

        // Robustere Extraktion der Video ID ohne zwingend das URL-Objekt (falls URL fehlerhaft)
        if (urlStr.includes('youtube.com/watch?v=')) {
          videoId = urlStr.split('v=')[1].split('&')[0];
        } else if (urlStr.includes('youtu.be/')) {
          videoId = urlStr.split('youtu.be/')[1].split('?')[0];
        } else if (urlStr.includes('youtube.com/embed/')) {
          videoId = urlStr.split('embed/')[1].split('?')[0];
        }

        if (videoId && /^[a-zA-Z0-9_-]+$/.test(videoId)) {
          return `
            <div class="video-container my-6 shadow-lg rounded-2xl overflow-hidden border border-gray-100" style="position: relative; width: 100%; padding-bottom: 56.25%; height: 0;">
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

    // Fallback für normale Bilder
    return `<img src="${urlValue}" alt="${text || ''}" title="${title || ''}" class="rounded-xl mx-auto shadow-sm" />`;
  };

  // HTML generieren mit dem angepassten Renderer
  // Wichtig: In neueren marked Versionen wird es so übergeben:
  const rawHtml = marked.parse(currentNote.value.content, { renderer: renderer });

  // DOMPurify Konfiguration
  return DOMPurify.sanitize(rawHtml, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling", "loading", "style"], // style erlaubt für das responsive Layout
  });
});
</script>

<style scoped>
/* Gewährleistet, dass eingebettete Videos die Breite nicht sprengen */
.video-container {
  width: 100%;
  clear: both;
}
</style>