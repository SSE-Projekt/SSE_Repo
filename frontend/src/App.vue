<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TopBar from "@/components/viewComponents/topBar.vue";

const route = useRoute()

// Zeige die Topbar nur auf den "App"-Seiten, nicht beim Login/Register
const showTopbar = computed(() => {
  return ['/notes', '/my-notes','/share-notes'].includes(route.path) || route.path.startsWith('/notes/')
})
</script>

<template>
  <div class="min-h-screen bg-[#fafafa]">
    <top-bar v-if="showTopbar" :active-view="route.path" />

    <router-view />
  </div>
</template>