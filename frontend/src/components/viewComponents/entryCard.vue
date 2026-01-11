<template>
  <div class="max-w-4xl mx-auto mt-12 p-6 bg-white border border-gray-200 rounded-2xl shadow-sm">

    <!-- TITLE -->
    <div class="mb-4 p-3 rounded-xl bg-gray-50 border border-gray-200">
      <input
          v-model="noteTitle"
          type="text"
          placeholder="Title..."
          class="w-full text-xl font-semibold outline-none bg-transparent text-gray-800 placeholder-gray-400"
      />
    </div>

    <!-- TEXT -->
    <div class="p-3 rounded-xl bg-white border border-gray-200">
      <textarea
          v-model="noteText"
          placeholder="Write a note... (Markdown supported)"
          class="w-full h-32 resize-none outline-none text-lg text-gray-700 placeholder-gray-400"
          @keydown.meta.enter="saveNote"
          @keydown.ctrl.enter="saveNote"
      ></textarea>
    </div>


    <div class="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
      <button
          @click="isPrivate = !isPrivate"
          class="flex items-center gap-2 text-gray-500 text-sm"
      >
        <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current">
          <path :d="isPrivate ? getIcon('lock-outline') : getIcon('earth')" />
        </svg>
        {{ isPrivate ? 'Private' : 'Public' }}
      </button>

      <button
          @click="saveNote"
          class="bg-[#9c9c9c] hover:bg-black text-white px-6 py-2 rounded-xl font-medium transition text-sm"
      >
        Create Note
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
</template>

<script setup>
import { ref, watch } from 'vue';
import { getIcon } from '@/utils/getIcon';
import DOMPurify from 'dompurify'; // Import für XSS Schutz

const noteTitle = ref('');
const noteText = ref('');
const isPrivate = ref(true);
const emit = defineEmits(['add-note', 'success', 'warn', 'error']);

const saveNote = () => {
  const pre_rawTitle = noteTitle.value?.trim();
  const rawContent = noteText.value?.trim();
  if(!pre_rawTitle) {
    emit('title-error');
    return;
  }
  if (!rawContent) {
    emit('content-error');
    return;
  }
  noteTitle.value = '# ' + noteTitle.value;
  const rawTitle = noteTitle.value?.trim();

  // 1. SCHNITTSTELLE SICHERN: Eingabebereinigung gegen XSS
  // Entfernt gefährliche HTML-Tags und Attribute (z.B. <script>, onload)
  // XSS SCHUTZ mit Erlaubnis für iFrames (für YouTube)
  // Wir erlauben iframes, damit die spätere Ersetzung funktionieren kann
  const cleanTitle = DOMPurify.sanitize(rawTitle)
  const cleanContent = DOMPurify.sanitize(rawContent, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling"]
  });

  if (!cleanTitle || !cleanContent) {
    // Feld leeren
    emit('error')
    noteText.value = '';
    return;
  }
  if (rawTitle !== cleanTitle || rawContent !== cleanContent || (cleanContent.includes('http') && !cleanContent.includes('(image-embed:') && !cleanContent.includes('(embed:')) || (cleanContent.includes('(embed:') && !cleanContent.includes('youtube') && !cleanContent.includes('youtu.be'))) {
    emit('warn')
    return;
  }

  const newNote = {
    id: self.crypto.randomUUID(), // Sichere ID für Direktlinks
    title: cleanTitle,
    content: cleanContent,
    isPrivate: isPrivate.value,
    date: new Date().toLocaleString(),
    author: "Aktueller Nutzer"    // Später durch echten Namen ersetzen
  };
  // 3. Im LocalStorage speichern
  // Zuerst bestehende Notizen laden oder leeres Array erstellen
  const existingNotes = JSON.parse(localStorage.getItem('notes') || '[]');

  // Neue Notiz am Anfang hinzufügen (unshift) oder Ende (push)
  existingNotes.unshift(newNote);

  // Array zurück in den LocalStorage schreiben
  localStorage.setItem('notes', JSON.stringify(existingNotes));

  // 4. Event an Parent senden (damit die UI sich sofort aktualisiert)
  emit('add-note', newNote);
  emit('success')
  // Feld leeren
  noteText.value = '';
  noteTitle.value = '';
};

</script>