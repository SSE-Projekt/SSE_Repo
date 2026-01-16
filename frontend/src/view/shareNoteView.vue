<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">Notes Shared and Received</h1>
        <p class="text-gray-500">Here are the notes that have been shared by yourself and received from other users.</p>

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

      <div class="mt-16">
        <h2 class="text-xl font-semibold mb-6 text-gray-800">All Shared Notes</h2>
        <div v-if="existingNotes.length > 0">
          <note-card v-for="(n, idx) in existingNotes" :key="idx" :note="n" />
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
import { getPublicNotes } from '@/services/api';  // ⭐ NEU!

const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const filter = ref('all');
const existingNotes = ref([]);  // ⭐ GEÄNDERT!
const loading = ref(false);

// ⭐ NEU: Shared Notes laden
// HINWEIS: "Shared Notes" Feature existiert noch nicht im Backend
// Momentan zeigen wir öffentliche Notizen
const loadNotes = async () => {
  loading.value = true;
  try {
    const notes = await getPublicNotes();
    
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
  return existingNotes.value.filter(note => {
    const content = (note.content || '').toLowerCase();
    const title = (note.title || '').toLowerCase();
    const query = (searchQuery.value || '').toLowerCase();
    const matchesSearch = content.includes(query) || title.includes(query);

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
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>