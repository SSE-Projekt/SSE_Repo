<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">My Notes</h1>
        <p class="text-gray-500">Here are all my notes.</p>

        <!-- ⭐ Loading State -->
        <div v-if="loading" class="mt-8 text-center text-gray-500">
          Lädt Notizen...
        </div>
        
        <!-- ⭐ Error State -->
        <div v-if="error" class="mt-8 bg-red-50 text-red-600 p-4 rounded-lg">
          {{ error }}
        </div>

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
              <note-card 
                v-for="note in filteredNotes" 
                :key="note.notizId"
                :note="adaptNoteForCard(note)"
                @click="router.push({ path: `/notes/${note.notizId}`, query: { from: 'my-notes' } })"
                class="cursor-pointer"
              />
            </div>

            <div v-else class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
              Keine Notizen zu "{{ searchQuery }}" gefunden.
            </div>
          </div>
        </div>
      </div>

      <entry-card
          @add-note="handleAddNote"
          @success="handleSuccess"
          @error="handleError"
          @warn="handleWarn"
      />

      <div class="mt-16">
        <h2 class="text-xl font-semibold mb-6 text-gray-800">Recent Notes</h2>
        
        <div v-if="!loading && existingNotes.length > 0">
          <note-card
              v-for="note in existingNotes"
              :key="note.notizId"
              :note="adaptNoteForCard(note)"
              @click="router.push({ path: `/notes/${note.notizId}`, query: { from: 'my-notes' } })"
              class="cursor-pointer"
          />
        </div>
        
        <div v-else-if="!loading" class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
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

// ⭐ NEU: Backend-API importieren
import { getMyNotes, deleteNote } from '@/services/api';

const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const filter = ref('all');

// ⭐ GEÄNDERT: Keine localStorage mehr!
const existingNotes = ref([]);
const loading = ref(false);
const error = ref(null);

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

// URL Logik
onMounted(async () => {
  if (route.query.q) searchQuery.value = route.query.q;
  if (route.query.type) filter.value = route.query.type;
  
  // ⭐ NEU: Notizen vom Backend laden
  await loadNotes();
});

// ⭐ NEU: Notizen vom Backend laden
const loadNotes = async () => {
  loading.value = true;
  error.value = null;
  
  try {
    const notes = await getMyNotes();
    existingNotes.value = notes;
  } catch (err) {
    console.error('Fehler beim Laden:', err);
    error.value = 'Fehler beim Laden der Notizen: ' + err.message;
  } finally {
    loading.value = false;
  }
};

const updateUrl = () => {
  router.replace({
    query: {
      q: searchQuery.value || undefined,
      type: filter.value !== 'all' ? filter.value : undefined
    }
  });
};

// ⭐ GEÄNDERT: Backend-Datenstruktur
const filteredNotes = computed(() => {
  return existingNotes.value.filter(note => {
    // Backend nutzt "notizText" und "title"
    const content = (note.notizText || '').toLowerCase();
    const title = (note.title || '').toLowerCase();
    const query = (searchQuery.value || '').toLowerCase();
    const matchesSearch = content.includes(query) || title.includes(query);

    // Backend nutzt "isPrivat"
    let matchesFilter = false;
    if (filter.value === 'all') {
      matchesFilter = true;
    } else if (filter.value === 'public') {
      matchesFilter = note.isPrivat === false;
    } else if (filter.value === 'private') {
      matchesFilter = note.isPrivat === true;
    }

    return matchesSearch && matchesFilter;
  });
});

// ⭐ NEU: Backend-Daten für note-card anpassen
const adaptNoteForCard = (note) => {
  return {
    id: note.notizId,           // Backend: notizId
    content: note.notizText,     // Backend: notizText
    title: note.title,
    isPrivate: note.isPrivat,    // Backend: isPrivat
    createdAt: note.createdAt
  };
};

// Handlers
const handleSuccess = async () => {
  snackbar.message = 'Notiz erfolgreich gespeichert!';
  snackbar.type = 'success';
  snackbar.show = true;
  
  // ⭐ GEÄNDERT: Vom Backend neu laden
  await loadNotes();
};

const handleWarn = (msg) => {
  snackbar.message = msg || 'Sicherheitsrisiko erkannt!';
  snackbar.type = 'warn';
  snackbar.show = true;
};

const handleError = (msg) => {
  snackbar.message = msg || 'Fehler beim Speichern!';
  snackbar.type = 'failed';
  snackbar.show = true;
};

// ⭐ NEU: Notiz löschen (Backend)
const handleDeleteNote = async (noteId) => {
  if (!confirm('Notiz wirklich löschen?')) return;
  
  try {
    await deleteNote(noteId);
    await loadNotes();
    
    snackbar.message = 'Notiz gelöscht!';
    snackbar.type = 'success';
    snackbar.show = true;
  } catch (err) {
    snackbar.message = 'Fehler beim Löschen: ' + err.message;
    snackbar.type = 'failed';
    snackbar.show = true;
  }
};

const handleAddNote = async () => {
  await loadNotes();
};
</script>