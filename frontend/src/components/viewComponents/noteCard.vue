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
import {computed} from 'vue';
import {useRouter} from 'vue-router';
import {marked} from 'marked';
import {getIcon} from '@/utils/getIcon';

const props = defineProps(['note']);
const router = useRouter();

const renderedContent = computed(() => {
  if (!props.note?.title) return '';

  const renderer = new marked.Renderer();

  // Spezial-Renderer für die Karten-Vorschau
  renderer.image = ({href, title, text}) => {
    const urlValue = typeof href === 'object' ? href.href : href;

    // Wenn es ein Video-Embed ist
    if (urlValue && urlValue.startsWith('embed:')) {
      // Wir rendern in der Vorschau kein Video, sondern einen schicken Link-Platzhalter
      return `<span class="text-blue-600 underline decoration-blue-400 font-medium flex items-center gap-1">
                <svg style="width:14px;height:14px;fill:currentColor" viewBox="0 0 24 24"><path d="M10,15L15.19,12L10,9V15M21.56,7.17C21.33,6.33 20.67,5.67 19.83,5.44C18.28,5 12,5 12,5C12,5 5.72,5 4.17,5.44C3.33,5.67 2.67,6.33 2.44,7.17C2,8.72 2,12 2,12C2,12 2,15.28 2.44,16.83C2.67,17.67 3.33,18.33 4.17,18.56C5.72,19 12,19 12,19C12,19 18.28,19 19.83,18.56C20.67,18.33 21.33,17.67 21.56,16.83C22,15.28 22,12 22,12C22,12 22,8.72 21.56,7.17Z" /></svg>
                Video: ${text || 'YouTube Link'}
              </span>`;
    } else if(urlValue && urlValue.startsWith('image-embed:')) {
      // Normales Bild in der Vorschau ebenfalls als Text-Link anzeigen (um Platz zu sparen)
      return `<span class="text-gray-500 italic">[Bild: ${text || 'Ohne Titel'}]</span>`;
    }
  };
  return marked.parse(props.note.content, {renderer});
});

const openDetails = () => {
  if (props.note?.id) {
    router.push(`/notes/${props.note.id}`);
  }
};
</script>

<style scoped>
.line-clamp-3 {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>