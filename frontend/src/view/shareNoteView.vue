<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">notes shared and collected</h1>
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
import NoteCard from "@/components/viewComponents/noteCard.vue";
import SnackBar from "@/components/viewComponents/snackBar.vue";

const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const filter = ref('all');
const existingNotes = ref(JSON.parse(localStorage.getItem('notes') || '[]'));

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

// --- URL Logik ---
onMounted(() => {
  if (route.query.q) searchQuery.value = route.query.q;
  if (route.query.type) filter.value = route.query.type;
});

const updateUrl = () => {
  router.replace({
    query: {
      q: searchQuery.value || undefined,
      type: filter.value !== 'all' ? filter.value : undefined
    }
  });
};

// --- Filterlogik (angepasst an isPrivate) ---
const filteredNotes = computed(() => {
  return existingNotes.value.filter(note => {
    // 1. Vorbereitung der Texte für die Recherche
    const content = (note.content || '').toLowerCase();
    const query = (searchQuery.value || '').toLowerCase();
    const matchesSearch = content.includes(query);

    // 2. Filterlogik
    let matchesFilter = false;

    if (filter.value === 'all') {
      matchesFilter = true;
    } else if (filter.value === 'public') {
      // Wenn isPrivate falsch ist (oder noch nicht existiert), ist es öffentlich.
      matchesFilter = note.isPrivate === false;
    } else if (filter.value === 'private') {
      // Wenn isPrivate wahr ist
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