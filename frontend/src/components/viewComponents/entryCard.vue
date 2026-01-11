<template>
  <div class="max-w-4xl mx-auto mt-12 p-6 bg-white border border-gray-200 rounded-2xl shadow-sm">
    
    <!-- ⭐ NEU: Title Input -->
    <input
        v-model="noteTitle"
        placeholder="Title (optional, max 50 characters)"
        maxlength="50"
        class="w-full mb-3 px-2 py-1 text-xl font-semibold outline-none text-gray-700 placeholder-gray-400 border-b border-gray-100"
    />
    
    <textarea
        v-model="noteText"
        placeholder="Write a note... (Markdown supported, max 500 characters)"
        maxlength="500"
        class="w-full h-32 resize-none outline-none text-lg text-gray-700 placeholder-gray-400"
        @keydown.meta.enter="saveNote"
        @keydown.ctrl.enter="saveNote"
    ></textarea>

    <div class="flex items-center justify-between mt-4 pt-4 border-t border-gray-100">
      <button @click="isPrivate = !isPrivate" class="flex items-center gap-2 text-gray-500 text-sm">
        <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current">
          <path :d="isPrivate ? getIcon('lock-outline') : getIcon('earth')" />
        </svg>
        {{ isPrivate ? 'Private' : 'Public' }}
      </button>

      <button 
        @click="saveNote" 
        :disabled="saving"
        class="bg-[#9c9c9c] hover:bg-black text-white px-6 py-2 rounded-xl font-medium transition text-sm disabled:opacity-50 disabled:cursor-not-allowed"
      >
        {{ saving ? 'Saving...' : 'Create Note' }}
      </button>
    </div>

    <div class="flex items-center justify-center gap-1 mt-6 text-gray-400 text-xs">
      <span>Press</span>
      <kbd class="bg-gray-50 px-1.5 py-0.5 rounded border border-gray-200 font-sans">⌘</kbd>
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
const saving = ref(false);

const emit = defineEmits(['add-note', 'success', 'warn', 'error']);

const saveNote = async () => {
  const rawContent = noteText.value?.trim();
  if (!rawContent) return;

  // XSS SCHUTZ (BEHALTEN!)
  const cleanContent = DOMPurify.sanitize(rawContent, {
    ADD_TAGS: ["iframe"],
    ADD_ATTR: ["allow", "allowfullscreen", "frameborder", "scrolling"]
  });

  if (!cleanContent) {
    emit('error', 'Inhalt konnte nicht bereinigt werden');
    noteText.value = '';
    return;
  }
  
  if (rawContent !== cleanContent) {
    emit('warn', 'Sicherheitsrisiko erkannt! Gefährlicher Code wurde entfernt.');
    return;
  }

  // ⭐ Backend API Call (wie beim Login!)
  saving.value = true;
  
  try {
    // Backend erwartet: { title, notizText, isPrivat }
    const noteData = {
      title: noteTitle.value?.trim() || 'Untitled',
      notizText: cleanContent,
      isPrivat: isPrivate.value
    };
    
    // API Call zum Backend
    const savedNote = await createNote(noteData);
    
    // Success!
    emit('add-note', savedNote);
    emit('success');
    
    // Reset
    noteTitle.value = '';
    noteText.value = '';
    isPrivate.value = true;
    
  } catch (error) {
    console.error('Fehler beim Speichern:', error);
    
    // Spezifische Fehler behandeln
    if (error.response?.status === 403) {
      emit('error', 'Du benötigst Schreibrechte (Rolle 2) um Notizen zu erstellen!');
    } else if (error.response?.status === 401) {
      emit('error', 'Session abgelaufen, bitte erneut anmelden!');
    } else {
      emit('error', 'Fehler beim Speichern: ' + (error.response?.data?.error || error.message));
    }
  } finally {
    saving.value = false;
  }
};
</script>