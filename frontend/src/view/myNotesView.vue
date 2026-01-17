<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">My Notes</h1>
        <p class="text-gray-500">Here are all my notes.</p>
        <SearchBar
            v-model="searchQuery"
            v-model:filterValue="filter"
            :notes="existingNotes"
            @update:modelValue="updateUrl"
            @update:filterValue="updateUrl"
        />
        <div class="mt-16">
          <div v-if="searchQuery">
            <h2 class="text-xl font-semibold mb-6 text-gray-800">Suchergebnisse</h2>

            <div v-if="filteredNotes.length > 0">
              <note-card v-for="(n, idx) in filteredNotes" :key="idx" :note="n" @click="router.push({ path: `/notes/${n.id}`, query: { from: 'my-notes' } })"
              />
            </div>

            <div v-else class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
              Keine Notizen zu "{{ searchQuery }}" gefunden.
            </div>
          </div>
        </div>
      </div>

      <entry-card
          @add-note="addNewNote"
          @success="handleSuccess"
          @error="handleError"
          @warn="handleWarn"
          @title-error="handleTitleError"
          @content-error="handleContentError"
      />

      <div class="mt-16">
        <h2 class="text-xl font-semibold mb-6 text-gray-800">Recent Notes</h2>
        <div v-if="existingNotes.length > 0">
          <note-card
              v-for="(n, idx) in existingNotes"
              :key="idx"
              :note="n"
              @click="router.push({ path: `/notes/${n.id}`, query: { from: 'my-notes' } })"
              class="cursor-pointer"
          />
        </div>
        <div v-else class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
          No notes yet. Start writing above!
        </div>
      </div>
    </main>

    <SnackBar
        v-model:show="snackbar.show"
        :message="snackbar.message"
        :type="snackbar.type"
    />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import SearchBar from "@/components/viewComponents/SearchBar.vue";
import EntryCard from "@/components/viewComponents/entryCard.vue";
import NoteCard from "@/components/viewComponents/noteCard.vue";
import SnackBar from "@/components/viewComponents/snackBar.vue";
import { getMyNotes } from '@/services/api';  // ⭐ NEU!

const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const filter = ref('all');
const existingNotes = ref([]);  // ⭐ NEU!
const loading = ref(false);     // ⭐ NEU!

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

// ⭐ Notizen vom Backend laden
const loadNotes = async () => {
  loading.value = true;
  try {
    const notes = await getMyNotes();
    
    // Backend-Daten für Frontend anpassen
    existingNotes.value = notes.map(note => ({
      id: note.notizId,
      title: note.title,
      content: note.notizText,
      isPrivate: note.isPrivat,
      date: note.createdAt
    }));
    
  } catch (error) {
    console.error('Fehler beim Laden:', error);
    snackbar.message = 'Fehler beim Laden der Notizen';
    snackbar.type = 'failed';
    snackbar.show = true;
  } finally {
    loading.value = false;
  }
};

// --- URL Logik ---
onMounted(async () => {  // ⭐ async hinzugefügt!
  if (route.query.q) searchQuery.value = route.query.q;
  if (route.query.type) filter.value = route.query.type;
  
  await loadNotes();  // ⭐ NEU: Notizen laden!
});

const updateUrl = () => {
  router.replace({
    query: {
      q: searchQuery.value || undefined,
      type: filter.value !== 'all' ? filter.value : undefined
    }
  });
};

// --- Filterlogik ---
const filteredNotes = computed(() => {
  return existingNotes.value.filter(note => {
    const content = (note.title || '').toLowerCase();
    const query = (searchQuery.value || '').toLowerCase();
    const matchesSearch = content.includes(query);

    let matchesFilter = false;

    if (filter.value === 'all') {
      matchesFilter = true;
    } else if (filter.value === 'public') {
      matchesFilter = note.isPrivate === false;
    } else if (filter.value === 'private') {
      matchesFilter = note.isPrivate === true;
    }

    return matchesSearch && matchesFilter;
  });
});

// --- Handlers ---
const handleSuccess = async () => {  // ⭐ async!
  snackbar.message = 'Notiz erfolgreich gespeichert!';
  snackbar.type = 'success';
  snackbar.show = true;
  
  await loadNotes();  // ⭐ NEU: Neu laden!
};

const handleWarn = (msg) => {
  snackbar.message = msg || 'Potentielles Sicherheitsrisiko erkannt!';
  snackbar.type = 'warn';
  snackbar.show = true;
};

const handleError = (msg) => {
  snackbar.message = msg || 'Tatsächliches Sicherheitsrisiko erkannt und entfernt!';
  snackbar.type = 'failed';
  snackbar.show = true;
};

const handleTitleError = (msg) => {
  snackbar.message = msg || 'Titel fehlt';
  snackbar.type = 'failed';
  snackbar.show = true;
};

const handleContentError = (msg) => {
  snackbar.message = msg || 'Text Content fehlt';
  snackbar.type = 'failed';
  snackbar.show = true;
};

const addNewNote = async () => {  // ⭐ async!
  await loadNotes();  // ⭐ GEÄNDERT: Backend statt localStorage!
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>