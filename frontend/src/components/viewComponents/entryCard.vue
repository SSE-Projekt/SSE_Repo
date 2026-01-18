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
import { ref } from 'vue';
import { getIcon } from '@/utils/getIcon';
import DOMPurify from 'dompurify';
import { createNote } from '@/services/api';  // ⭐ NEU!

const noteTitle = ref('');
const noteText = ref('');
const isPrivate = ref(true);
const saving = ref(false);  // ⭐ NEU!

const emit = defineEmits(['add-note', 'success', 'warn', 'error', 'title-error', 'content-error']);

const saveNote = async () => {  // ⭐ async!
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

  // XSS SCHUTZ
  const cleanTitle = DOMPurify.sanitize(rawTitle);
  const cleanContent = DOMPurify.sanitize(rawContent, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling"]
  });

  if (!cleanTitle || !cleanContent) {
    emit('error');
    noteText.value = '';
    return;
  }
  
  if (rawTitle !== cleanTitle || rawContent !== cleanContent || 
      (cleanContent.includes('http') && !cleanContent.includes('(image-embed:') && !cleanContent.includes('(embed:')) || 
      (cleanContent.includes('(embed:') && !cleanContent.includes('youtube') && !cleanContent.includes('youtu.be'))) {
    emit('warn');
    return;
  }

  // ⭐ AN BACKEND SENDEN!
  saving.value = true;
  
  try {
    const noteData = {
      title: cleanTitle,
      notizText: cleanContent,  // Backend erwartet "notizText"!
      isPrivat: isPrivate.value  // Backend erwartet "isPrivat"!
    };
    
    const savedNote = await createNote(noteData);
    
    // Event an Parent senden
    emit('add-note', savedNote);
    emit('success');
    
    // Felder leeren
    noteText.value = '';
    noteTitle.value = '';
    isPrivate.value = true;
    
  } catch (error) {
    console.error('Fehler beim Speichern:', error);
    
    if (error.response?.status === 403) {
      emit('error', 'Keine Schreibrechte!');
    } else if (error.response?.status === 401) {
      emit('error', 'Bitte erneut anmelden!');
    } else {
      emit('error', 'Fehler beim Speichern: ' + error.message);
    }
  } finally {
    saving.value = false;
  }
};
</script>