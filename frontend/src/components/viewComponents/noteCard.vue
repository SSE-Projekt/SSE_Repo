<template>
  <div
      @click="openDetails"
      class="p-6 bg-white border border-gray-200 rounded-2xl shadow-sm mb-4 cursor-pointer hover:border-blue-400 hover:shadow-md transition-all group"
  >
    <div class="prose prose-sm max-w-none mb-6 text-gray-800 line-clamp-3" v-html="renderedContent"></div>

    <div class="flex items-center justify-between border-t border-gray-50 pt-4">
      <div class="flex items-center gap-4 text-xs font-medium">
        <div class="flex items-center gap-1" :class="note?.isPrivate ? 'text-gray-500' : 'text-blue-600'">
          <svg viewBox="0 0 24 24" class="w-3.5 h-3.5 fill-current">
            <path :d="note?.isPrivate ? getIcon('lock') : getIcon('earth')" />
          </svg>
          {{ note?.isPrivate ? 'Private' : 'Public' }}
        </div>

        <div class="flex items-center gap-1 text-gray-400">
          <svg viewBox="0 0 24 24" class="w-3.5 h-3.5 fill-current">
            <path :d="getIcon('clock-outline')" />
          </svg>
          {{ note?.date }}
        </div>
      </div>

      <div
          @click="openDetails"
          class="text-blue-500 opacity-0 group-hover:opacity-100 transition-opacity text-xs font-semibold flex items-center gap-1"
      >
        Details ansehen
        <svg viewBox="0 0 24 24" class="w-3.5 h-3.5 fill-current">
          <path :d="getIcon('arrow-right')" />
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { marked } from 'marked';
import { getIcon } from '@/utils/getIcon';

const props = defineProps(['note']);
const router = useRouter();

// Markdown-Inhalt für die Vorschau rendern
const renderedContent = computed(() => {
  return props.note?.content ? marked.parse(props.note.content) : '';
});

// Navigation zur Detailansicht über die ID
const openDetails = () => {
  if (props.note?.id) {
    router.push(`/notes/${props.note.id}`);
  }
};
</script>

<style scoped>
/* Kürzt den Text nach 3 Zeilen ab, falls er zu lang ist */
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>