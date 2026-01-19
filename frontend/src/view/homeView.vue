<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">Notes</h1>
        <p class="text-gray-500">Capture your thoughts in Markdown</p>

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
              <note-card v-for="(n, idx) in filteredNotes" :key="idx" :note="n" />
            </div>

            <div v-else class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
              Keine Notizen zu "{{ searchQuery }}" gefunden.
            </div>
          </div>
        </div>
      </div>

      <entry-card
          v-if="user?.user_metadata.user_rolle === 2"
          @add-note="addNewNote"
          @success="handleSuccess"
          @error="handleError"
          @warn="handleWarn"
          @title-error="handleTitleError"
          @content-error="handleContentError"
      />

      <div v-else class="max-w-4xl mx-auto mt-12 p-6 bg-blue-50 border border-blue-100 rounded-2xl text-center text-blue-600 text-sm">
        Sie sind als <strong>Leser</strong> angemeldet. Erstellen von Notizen ist Autoren vorbehalten.
      </div>

      <div class="mt-16">
        <h2 class="text-xl font-semibold mb-6 text-gray-800">Recent Notes</h2>
        <div v-if="existingNotes.length > 0">
          <note-card v-for="(n, idx) in existingNotes" :key="idx" :note="n" />
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
import { getMyNotes, getPublicNotes } from '@/services/api';  // ⭐ NEU!

const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const filter = ref('all');
const existingNotes = ref([]);  // ⭐ GEÄNDERT!
const loading = ref(false);

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

const user = ref(null);

const storedUser = localStorage.getItem('user');
user.value = storedUser ? JSON.parse(storedUser) : null;

// ⭐ NEU: Notizen vom Backend laden
const loadNotes = async () => {
  loading.value = true;
  try {
    let myNotes = [];
    let publicNotes = [];
    
    // Immer öffentliche Notizen laden
    publicNotes = await getPublicNotes();
    
    // Eigene Notizen laden (falls eingeloggt)
    try {
      myNotes = await getMyNotes();
    } catch (error) {
      // Falls nicht eingeloggt, nur öffentliche anzeigen
      console.log('Eigene Notizen nicht verfügbar');
    }
    
    // Kombinieren ohne Duplikate (basierend auf ID)
    const allNotes = [...myNotes];
    publicNotes.forEach(pubNote => {
      if (!allNotes.find(n => n.notizId === pubNote.notizId)) {
        allNotes.push(pubNote);
      }
    });
    
    // Backend-Daten für Frontend anpassen
    existingNotes.value = allNotes.map(note => ({
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
onMounted(async () => {
  if (route.query.q) searchQuery.value = route.query.q;
  if (route.query.type) filter.value = route.query.type;
  
  // ⭐ NEU: Notizen laden
  await loadNotes();
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
  // Sicherheit: Falls existingNotes noch nicht geladen ist, leeres Array zurückgeben
  if (!existingNotes.value) return [];

  return existingNotes.value.filter(note => {
    // 1. Vorbereitung der Texte für die Recherche
    // Wir nehmen den Titel UND den Text für die Suche
    const noteTitle = (note.title || '').toLowerCase();
    const noteText = (note.notizText || '').toLowerCase(); // Falls dein Feld in der DB 'notizText' heißt
    const query = (searchQuery.value || '').toLowerCase();

    // FEHLER BEHOBEN: Wir nutzen noteTitle statt des undefinierten 'title'
    const matchesSearch = noteTitle.includes(query) || noteText.includes(query);

    let matchesFilter = false;

    if (filter.value === 'all') {
      matchesFilter = true;
    } else if (filter.value === 'public') {
      // Prüfe, ob die Property 'isPrivat' (wie in deinem Hibernate-Log) oder 'isPrivate' heißt
      matchesFilter = note.isPrivat === false || note.isPrivate === false;
    } else if (filter.value === 'private') {
      matchesFilter = note.isPrivat === true || note.isPrivate === true;
    }

    return matchesSearch && matchesFilter;
  });
});

// --- Handlers ---
const handleSuccess = async () => {
  snackbar.message = 'Notiz erfolgreich gespeichert!';
  snackbar.type = 'success';
  snackbar.show = true;
  
  // ⭐ NEU: Notizen neu laden
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

const addNewNote = async () => {
  // ⭐ GEÄNDERT: Vom Backend neu laden
  await loadNotes();
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