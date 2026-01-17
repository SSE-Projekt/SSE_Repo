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

        <div class="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
          <button
              @click="editNote.isPrivate = !editNote.isPrivate"
              class="flex items-center gap-2 text-gray-500 text-sm"
          >
            <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current">
              <path :d="editNote.isPrivate ? getIcon('lock-outline') : getIcon('earth')" />
            </svg>
            {{ editNote.isPrivate ? 'Private' : 'Public' }}
          </button>

          <button
              @click="saveChanges"
              class="bg-black text-white px-8 py-3 rounded-xl font-bold hover:bg-gray-800 transition-all shadow-lg active:scale-[0.98]"
          >
            Änderungen speichern
          </button>
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

const route = useRoute();
const router = useRouter();
const editNote = ref(null);

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

onMounted(() => {
  const notes = JSON.parse(localStorage.getItem('notes') || '[]');
  // On récupère la note sans le "# " au début du titre pour l'input
  const note = notes.find(n => n.id === route.params.id);

  if (note) {
    editNote.value = {
      ...note,
      title: note.title.startsWith('# ') ? note.title.substring(2) : note.title
    };
  } else {
    router.push('/my-notes');
  }
});

const showMsg = (msg, type) => {
  snackbar.message = msg;
  snackbar.type = type;
  snackbar.show = true;
};

const saveChanges = () => {
  const preRawTitle = editNote.value.title?.trim();
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

  // 2. Vérification de sécurité stricte
  if (!cleanTitle || !cleanContent) {
    showMsg('Tatsächliches Sicherheitsrisiko erkannt und entfernt!', 'failed');
    return;
  }

  // Détection des liens non sécurisés ou mal formatés (Identique à EntryCard)
  const hasUnsafeLinks = (cleanContent.includes('http') && !cleanContent.includes('(image-embed:') && !cleanContent.includes('(embed:'));
  const hasUnsafeEmbeds = (cleanContent.includes('(embed:') && !cleanContent.includes('youtube') && !cleanContent.includes('youtu.be'));

  if (fullTitle !== cleanTitle || rawContent !== cleanContent || hasUnsafeLinks || hasUnsafeEmbeds) {
    showMsg('Potentielles Sicherheitsrisiko erkannt!', 'warn');
    return;
  }

  // 3. Mise à jour du LocalStorage
  const allNotes = JSON.parse(localStorage.getItem('notes') || '[]');
  const index = allNotes.findIndex(n => n.id === editNote.value.id);

  if (index !== -1) {
    allNotes[index] = {
      ...allNotes[index],
      title: cleanTitle,
      content: cleanContent,
      isPrivate: editNote.value.isPrivate,
      date: new Date().toLocaleString() // On met à jour la date
    };

    localStorage.setItem('notes', JSON.stringify(allNotes));

    showMsg('Änderungen erfolgreich gespeichert!', 'success');

    // Redirection après succès
    setTimeout(() => {
      router.replace({
        path: `/notes/${editNote.value.id}`,
        query: { from: 'my-notes' }
      });
    }, 1500);
  }
};
</script>