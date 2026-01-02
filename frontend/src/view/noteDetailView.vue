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

// Die ID kommt direkt aus dem Router dank props: true
const props = defineProps({
  id: String
});

const router = useRouter();

// HINWEIS: In einer echten App würdest du hier die Notiz aus deinem
// Store (Pinia) oder via API-Call anhand der props.id laden.
// Hier als Beispiel ein lokaler Mock-Check:
const notesStore = JSON.parse(localStorage.getItem('notes') || '[]');
const currentNote = computed(() => notesStore.find(n => n.id === props.id));

const renderedContent = computed(() => {
  return currentNote.value ? marked.parse(currentNote.value.content) : '';
});

// In NoteDetailView.vue
import DOMPurify from 'dompurify';

const renderedContents = computed(() => {
  if (!currentNote.value?.content) return '';
  // Doppelte Sicherheit: Sowohl beim Speichern als auch beim Rendern sanitizen
  const html = marked.parse(currentNote.value.content);
  return DOMPurify.sanitize(html.trim());
});
</script>