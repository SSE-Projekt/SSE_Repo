<template>
  <nav class="flex items-center justify-between px-8 py-4 bg-white border-b border-gray-100 sticky top-0 z-10">
    <div class="flex items-center gap-2 text-xl font-semibold cursor-pointer" @click="$emit('change-view', 'home')">
      <div class="bg-black text-white w-8 h-8 flex items-center justify-center rounded-md text-sm font-bold">N</div>
      <span>Notes</span>
    </div>

    <div class="flex items-center gap-2 bg-gray-50 p-1 rounded-xl">

      <button
          @click="$emit('change-view', 'home')"
          :class="activeView === 'home' ? 'bg-white shadow-sm text-black font-medium' : 'text-gray-500 hover:text-black'"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all"
      >
        <router-link to="/home" class="nav-item flex items-center gap-2">
          <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current"><path :d="getIcon('home')" /></svg>
          Home
        </router-link>
      </button>

      <button
          v-if="userRole === 'autor'"
          @click="$emit('change-view', 'my-notes')"
          :class="activeView === 'my-notes' ? 'bg-white shadow-sm text-black font-medium' : 'text-gray-500 hover:text-black'"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all"
      >
        <router-link to="/my-notes" class="nav-item flex items-center gap-2">
          <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current"><path :d="getIcon('file-document-outline')" /></svg>
          My Notes
        </router-link>
      </button>

      <button
          @click="$emit('change-view', 'share-notes')"
          :class="activeView === 'share-notes' ? 'bg-white shadow-sm text-black font-medium' : 'text-gray-500 hover:text-black'"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all"
      >
        <router-link to="/share-notes" class="nav-item flex items-center gap-2">
          <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current"><path :d="getIcon('share-variant')" /></svg>
          Shared Notes
        </router-link>
      </button>
    </div>

    <div class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center text-gray-400 cursor-pointer hover:bg-gray-200 transition">
      <svg viewBox="0 0 24 24" class="w-6 h-6 fill-current"><path :d="getIcon('account')" /></svg>
    </div>
  </nav>
</template>

<script setup>
import { getIcon } from '@/utils/getIcon';

// Ajout de userRole dans les props
defineProps(['activeView', 'userRole']);
defineEmits(['change-view']);
</script>