<template>
  <div class="min-h-screen bg-[#fafafa] py-12 px-4">
    <div v-if="editNote" class="max-w-4xl mx-auto bg-white border border-gray-200 rounded-3xl shadow-sm p-8">

      <div class="flex items-center justify-between mb-8">
        <h1 class="text-2xl font-bold text-gray-900">Notiz bearbeiten</h1>
        <button @click="router.back()" class="text-sm text-gray-500 hover:text-black transition-colors">
          Abbrechen
        </button>
      </div>

      <div class="space-y-6">
        <div class="p-3 rounded-xl bg-gray-50 border border-gray-200">
          <input
              v-model="editNote.title"
              type="text"
              placeholder="Title..."
              class="w-full text-xl font-semibold outline-none bg-transparent text-gray-800 placeholder-gray-400"
          />
        </div>

        <div class="p-3 rounded-xl bg-white border border-gray-200">
          <textarea
              v-model="editNote.content"
              placeholder="Write a note... (Markdown supported)"
              class="w-full h-64 resize-none outline-none text-lg text-gray-700 placeholder-gray-400"
              @keydown.meta.enter="saveChanges"
              @keydown.ctrl.enter="saveChanges"
          ></textarea>
        </div>

        <div class="flex items-center gap-4 p-4 bg-gray-50 rounded-2xl border border-gray-100">
          <div class="flex items-center gap-2">
            <input type="checkbox" v-model="editNote.isPrivat" id="private-check" class="w-4 h-4 accent-black" />
            <label for="private-check" class="text-sm text-gray-700 cursor-pointer">Privat halten</label>
          </div>
        </div>

        <div class="flex items-center justify-center gap-1 mt-6 text-gray-400 text-xs">
          <span>Press</span>
          <kbd class="bg-gray-50 px-1.5 py-0.5 rounded border border-gray-200 font-sans">⌘ ctrl</kbd>
          <span>+</span>
          <kbd class="bg-gray-50 px-1.5 py-0.5 rounded border border-gray-200 font-sans">Enter</kbd>
          <span>to save</span>
        </div>
      </div>
    </div>

    <div v-else class="text-center py-20">
      <p class="text-gray-500 italic">Lade Notiz...</p>
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
import { getIcon } from '@/utils/getIcon';
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
      isPrivat: note.isPrivat,
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

const showMsg = (msg, type) => {
  snackbar.message = msg;
  snackbar.type = type;
  snackbar.show = true;
};

const saveChanges = async () => {
  const sanitizedTitle = DOMPurify.sanitize(editNote.value.title || '');
  const rawContent = editNote.value.content?.trim();

  // Validation des champs vides (comme dans EntryCard)
  if (!preRawTitle) {
    showMsg('Titel fehlt', 'failed');
    return;
  }
  if (!rawContent) {
    showMsg('Text Content fehlt', 'failed');
    return;
  }

  // Préparation du titre avec Markdown
  const fullTitle = '# ' + preRawTitle;

  // 1. Protection XSS (Identique à EntryCard)
  const cleanTitle = DOMPurify.sanitize(fullTitle);
  const cleanContent = DOMPurify.sanitize(rawContent, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling"]
  });

  if (!cleanContent) {
    handleError('Inhalt ungültig!');
    editNote.value.content = '';
    return;
  }

  // Détection des liens non sécurisés ou mal formatés (Identique à EntryCard)
  const hasUnsafeLinks = (cleanContent.includes('http') && !cleanContent.includes('(image-embed:') && !cleanContent.includes('(embed:'));
  const hasUnsafeEmbeds = (cleanContent.includes('(embed:') && !cleanContent.includes('youtube') && !cleanContent.includes('youtu.be'));

  if (fullTitle !== cleanTitle || rawContent !== cleanContent || hasUnsafeLinks || hasUnsafeEmbeds) {
    showMsg('Potentielles Sicherheitsrisiko erkannt!', 'warn');
    return;
  }


  try {
    const savedNote = {
      id: editNote.value.id,
      title: editNote.value.title,
      notizText: editNote.value.content,
      isPrivat: editNote.value.isPrivat,
    };
    await updateNote(editNote.value.id, savedNote);

    // Redirection après succès
    setTimeout(() => {
      router.replace({
        path: `/notes/${editNote.value.id}`,
        query: {from: '/my-notes'}
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
}
</script>