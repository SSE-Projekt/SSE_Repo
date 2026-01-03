<template>
  <div class="bg-white p-6 rounded-3xl border border-gray-200 shadow-sm mb-12">
    <div class="flex flex-col md:flex-row gap-4">
      <div class="relative flex-1">
        <input
            :value="modelValue"
            @input="$emit('update:modelValue', $event.target.value)"
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
// On définit les propriétés pour le "v-model" double
defineProps(['modelValue', 'filterValue']);
defineEmits(['update:modelValue', 'update:filterValue']);
</script>