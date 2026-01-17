<template>
  <div class="min-h-screen bg-[#fafafa] py-12 px-4">
    <div v-if="editNote" class="max-w-3xl mx-auto bg-white border border-gray-200 rounded-3xl shadow-sm p-8">

      <div class="flex items-center justify-between mb-8">
        <h1 class="text-2xl font-bold text-gray-900">Notiz bearbeiten</h1>
        <button @click="router.back()" class="text-sm text-gray-500 hover:text-black transition-colors">
          Abbrechen
        </button>
      </div>

      <div class="space-y-6">
        <div class="space-y-2">
          <label class="text-sm font-semibold text-gray-700">neue Notiz</label>
          <input
              v-model="editNote.title"
              type="text"
              class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all"
              placeholder="Titel deiner Notiz..."
          />
          <textarea
              v-model="editNote.content"
              rows="12"
              class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all font-mono text-sm"
              placeholder="Schreiben Sie ihre bearbeite Notiz."
          ></textarea>
        </div>

        <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100">
          <div class="flex items-center gap-2">
            <input type="checkbox" v-model="editNote.isPrivate" id="private-check" class="w-4 h-4 accent-black" />
            <label for="private-check" class="text-sm text-gray-700 cursor-pointer">Privat halten</label>
          </div>
        </div>

        <button
            @click="saveChanges"
            class="w-full py-4 bg-black text-white rounded-2xl font-bold hover:bg-gray-800 transition-all shadow-lg active:scale-[0.98]"
        >
          Änderungen speichern
        </button>
      </div>
    </div>

    <div v-else class="text-center py-20">
      <p class="text-gray-500">Lade Notiz...</p>
    </div>

    <SnackBar
        v-model:show="snackbar.show"
        :message="snackbar.message"
        :type="snackbar.type"
    />
  </div>

</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import DOMPurify from 'dompurify';
import SnackBar from "@/components/viewComponents/snackBar.vue";
import { getNote, updateNote } from '@/services/api';  // ⭐ NEU!

const route = useRoute();
const router = useRouter();
const editNote = ref(null);
const loading = ref(false);  // ⭐ NEU!

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

// ⭐ NEU: Notiz vom Backend laden
onMounted(async () => {
  loading.value = true;
  try {
    const note = await getNote(route.params.id);
    
    // Backend-Daten für Frontend anpassen
    editNote.value = {
      id: note.notizId,
      content: note.notizText,
      isPrivate: note.isPrivat,
      title: note.title
    };
    
  } catch (error) {
    console.error('Fehler beim Laden:', error);
    handleError('Notiz nicht gefunden');
    router.push('/my-notes');
  } finally {
    loading.value = false;
  }
});

const handleSuccess = () => {
  snackbar.message = 'Änderungen erfolgreich gespeichert!';
  snackbar.type = 'success';
  snackbar.show = true;
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

const saveChanges = () => {
  const sanitizedTitle = DOMPurify.sanitize(editNote.value.title || '');
  const rawContent = editNote.value.content?.trim();
  if (!rawContent) return;

  const cleanContent = DOMPurify.sanitize(rawContent, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling"]
  });

  if (!cleanContent) {
    handleError('Inhalt ungültig!');
    editNote.value.content = '';
    return;
  }

  if (rawContent !== cleanContent) {
    handleWarn();
    return;
  }


  const allNotes = JSON.parse(localStorage.getItem('notes') || '[]');
  const index = allNotes.findIndex(n => n.id === editNote.value.id);

  if (index !== -1) {
    allNotes[index] = {
      ...allNotes[index],
      title: sanitizedTitle,
      content: cleanContent,
      isPrivate: editNote.value.isPrivate,
      lastEdit: new Date().toLocaleString()
    };
    
    await updateNote(editNote.value.id, noteData);
    
    handleSuccess();

    setTimeout(() => {
      router.replace({
        path: `/notes/${editNote.value.id}`,
        query: { from: 'my-notes' }
      });
    }, 1500);
    
  } catch (error) {
    console.error('Fehler beim Speichern:', error);
    
    if (error.response?.status === 403) {
      handleError('Keine Berechtigung zum Bearbeiten!');
    } else if (error.response?.status === 401) {
      handleError('Bitte erneut anmelden!');
    } else {
      handleError('Fehler beim Speichern: ' + error.message);
    }
  } finally {
    loading.value = false;
  }
};
</script>