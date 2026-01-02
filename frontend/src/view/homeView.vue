<template>
  <div class="min-h-screen bg-[#fafafa]">
    <main class="max-w-4xl mx-auto px-4 py-16">
      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold mb-2 text-gray-900">Notes</h1>
        <p class="text-gray-500">Capture your thoughts in Markdown</p>
      </div>

      <entry-card @add-note="addNewNote()" @success="handleSuccess()" @error="handleError('Dein Eintrag stellt ein reales Sicherheitsrisiko dar')" @warn="handleWarn('Dein Eintrag stellt ein potentielles Sicherheitsrisiko dar, kann nicht gespeichert werden')" />

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
        :timeout="3000"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import EntryCard from "@/components/viewComponents/entryCard.vue";
import NoteCard from "@/components/viewComponents/noteCard.vue";
import SnackBar from "@/components/viewComponents/snackBar.vue";
import { reactive } from 'vue';

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

const handleSuccess = () => {
  snackbar.message = 'Notiz erfolgreich gespeichert!';
  snackbar.type = 'success';
  snackbar.show = true;
};

const handleWarn = (msg) => {
  snackbar.message = msg || 'Problem beim Speichern!';
  snackbar.type = 'warn';
  snackbar.show = true;
};

const handleError = (msg) => {
  snackbar.message = msg || 'Fehler beim Speichern!';
  snackbar.type = 'failed';
  snackbar.show = true;
};

const existingNotes = ref(JSON.parse(localStorage.getItem('notes') || '[]'));
const addNewNote = () => {
  existingNotes.value = JSON.parse(localStorage.getItem('notes') || '[]');
}

</script>