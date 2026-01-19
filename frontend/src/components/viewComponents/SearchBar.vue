<template>
  <div class="bg-white p-6 rounded-3xl border border-gray-200 shadow-sm mb-12">
    <div class="flex flex-col md:flex-row gap-4">
      <div class="relative flex-1">
        <input
            :value="modelValue"
            @input="handleInput"
            type="text"
            placeholder="Notizen durchsuchen..."
            class="w-full pl-4 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all"
        />
      </div>

      <div class="flex gap-2">
        <select
            :value="filterValue"
            @change="$emit('update:filterValue', $event.target.value)"
            class="px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black cursor-pointer"
        >
          <option value="all">Alle Notizen</option>
          <option value="private">Nur Eigene (Privat)</option>
          <option value="public">Nur Öffentliche</option>
        </select>
      </div>
    </div>
    <div v-if="modelValue" class="mt-4 text-sm text-gray-600">
      Ergebnisse für: <span class="font-bold">"{{ modelValue }}"</span>
      <span v-if="filterValue !== 'all'" class="italic"> (Filter: {{ filterValue }})</span>
    </div>
  </div>
</template>

<script setup>
import DOMPurify from 'dompurify';

const props = defineProps(['modelValue', 'filterValue']);
const emit = defineEmits(['update:modelValue', 'update:filterValue']);

const handleInput = (event) => {
  const rawValue = event.target.value;
  const cleanValue = DOMPurify.sanitize(rawValue);

  emit('update:modelValue', cleanValue);
};
</script>