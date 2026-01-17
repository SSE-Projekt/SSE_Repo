<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">Notes Shared and Received</h1>
        <p class="text-gray-500">Here are the notes that have been shared with you.</p>

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
              <div v-for="(n, idx) in filteredNotes" :key="idx" class="relative mb-4">
                <note-card :note="n" />
                <span class="absolute top-2 right-2 text-xs font-medium bg-blue-100 text-blue-800 px-2 py-1 rounded-full">
                  Von: {{ n.sharedBy }}
                </span>
              </div>
            </div>

            <div v-else class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
              Keine Notizen zu "{{ searchQuery }}" gefunden.
            </div>
          </div>
        </div>
      </div>

      <div class="mt-16">
        <h2 class="text-xl font-semibold mb-6 text-gray-800">All Shared Notes</h2>
        <div v-if="existingNotes.length > 0">
          <div v-for="(n, idx) in existingNotes" :key="n.id" class="relative mb-4">
            <note-card :note="n" />
            <span class="absolute top-4 right-4 text-xs font-semibold text-gray-400">
              Geteilt von: {{ n.sharedBy }}
            </span>
          </div>
        </div>
        <div v-else class="text-center py-12 text-gray-400 bg-white rounded-2xl border border-dashed border-gray-200">
          No notes yet. Start sharing notes!
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import SearchBar from "@/components/viewComponents/SearchBar.vue";
import NoteCard from "@/components/viewComponents/noteCard.vue";
import { getSharedNotes } from '@/services/api'; 
import { shareNote } from '@/services/api';

const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const filter = ref('all');
const existingNotes = ref([]);
const loading = ref(false);

const loadNotes = async () => {
  loading.value = true;
  try {
    const sharedData = await getSharedNotes();
    
    // Daten-Mapping vom Backend zum Frontend-Format
    existingNotes.value = sharedData.map(item => ({
      id: item.note.notizId,
      title: item.note.title,
      content: item.note.notizText,
      isPrivate: item.note.isPrivat,
      date: item.note.createdAt,
      sharedBy: item.sharedBy 
    }));
    
  } catch (error) {
    console.error('Fehler beim Laden der geteilten Notizen:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  if (route.query.q) searchQuery.value = route.query.q;
  if (route.query.type) filter.value = route.query.type;
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

const filteredNotes = computed(() => {
  return existingNotes.value.filter(note => {
    // 1. Vorbereitung der Texte für die Recherche
    const content = (note.title || '').toLowerCase();
    const query = (searchQuery.value || '').toLowerCase();
    const matchesSearch = content.includes(query) || title.includes(query);

    let matchesFilter = filter.value === 'all' || 
                       (filter.value === 'public' && !note.isPrivate) || 
                       (filter.value === 'private' && note.isPrivate);

    return matchesSearch && matchesFilter;
  });
});

const handleShare = async (noteId) => {
  const email = prompt("Email des Empfängers eingeben:"); // Einfache Demo-Lösung
  if (email) {
    try {
      const result = await shareNote(noteId, email);
      alert(result.message); // "Note erfolgreich geteilt!"
    } catch (error) {
      // Zeigt die Fehlermeldungen vom Backend an (z.B. "User nicht gefunden")
      alert("Fehler: " + (error.response?.data?.error || "Unbekannter Fehler"));
    }
  }
};

const confirmShare = async (noteId, targetEmail) => {
  try {
    // SSE-PRINZIP: Wir senden die Anfrage an das Backend
    const response = await shareNote(noteId, targetEmail);
    
    // Erfolgsmeldung anzeigen (z.B. über ein Toast oder Alert)
    console.log(response.message);
    
    // Schließe deine Modal hier
    isShareModalOpen.value = false;
    
  } catch (error) {
    // Fehlerbehandlung: Zeige dem User, was schiefgelaufen ist
    const errorMsg = error.response?.data?.error || "Ein Fehler ist aufgetreten";
    alert("Fehler beim Teilen: " + errorMsg);
    
    // SSE-HINWEIS: Logge den Fehler für Debugging, aber zeige dem User 
    // nur sichere Fehlermeldungen.
    console.error("Share-Error:", error);
  }
};
</script>