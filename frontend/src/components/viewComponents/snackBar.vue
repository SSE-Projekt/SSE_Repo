<template>
  <Transition name="snackbar-fade">
    <div
        v-if="isVisible"
        class="fixed bottom-8 left-1/2 -translate-x-1/2 z-[100] min-w-[300px] max-w-md px-6 py-4 rounded-2xl shadow-2xl border flex items-center gap-3"
        :class="styleClasses"
    >
      <svg viewBox="0 0 24 24" class="w-6 h-6 fill-current shrink-0">
        <path :d="iconPath" />
      </svg>

      <span class="font-medium text-sm">{{ message }}</span>

      <button @click="close" class="ml-auto hover:opacity-70 transition">
        <svg viewBox="0 0 24 24" class="w-4 h-4 fill-current">
          <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
        </svg>
      </button>
    </div>
  </Transition>
</template>

<script setup>
import { ref, watch, computed } from 'vue';
import { getIcon } from '@/utils/getIcon';

const props = defineProps({
  message: String,
  type: {
    type: String,
    default: 'success', // 'success', 'warn', 'failed'
  },
  timeout: {
    type: Number,
    default: 3000
  },
  show: Boolean // Steuerung der Sichtbarkeit von außen
});

const emit = defineEmits(['update:show']);
const isVisible = ref(false);
let timer = null;

// Klassen basierend auf dem Typ
const styleClasses = computed(() => {
  switch (props.type) {
    case 'success': return 'bg-emerald-50 border-emerald-200 text-emerald-800';
    case 'warn': return 'bg-amber-50 border-amber-200 text-amber-800';
    case 'failed': return 'bg-rose-50 border-rose-200 text-rose-800';
    default: return 'bg-gray-50 border-gray-200 text-gray-800';
  }
});

// Icon Pfad holen
const iconPath = computed(() => {
  if (props.type === 'success') return getIcon('check-circle');
  if (props.type === 'warn') return getIcon('alert');
  if (props.type === 'failed') return getIcon('alert-circle');
  return getIcon('information');
});

const close = () => {
  isVisible.value = false;
  emit('update:show', false);
  if (timer) clearTimeout(timer);
};

// Überwache die 'show' Prop
watch(() => props.show, (newVal) => {
  if (newVal) {
    isVisible.value = true;
    if (timer) clearTimeout(timer);
    timer = setTimeout(() => {
      close();
    }, props.timeout);
  } else {
    isVisible.value = false;
  }
});
</script>

<style scoped>
.snackbar-fade-enter-active,
.snackbar-fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.snackbar-fade-enter-from {
  opacity: 0;
  transform: translate(-50%, 20px);
}
.snackbar-fade-leave-to {
  opacity: 0;
  transform: translate(-50%, 10px) scale(0.95);
}
</style>