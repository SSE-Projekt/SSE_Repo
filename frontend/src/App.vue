<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import TopBar from "@/components/viewComponents/topBar.vue";

const route = useRoute()

const user = JSON.parse(localStorage.getItem('user'))?? null
const userRole = computed(() => user? user.user_metadata.user_rolle : 0)
// Zeige die Topbar nur auf den "App"-Seiten, nicht beim Login/Register
const showTopbar = computed(() => {
  return ['/notes', '/my-notes','/share-notes'].includes(route.path) || route.path.startsWith('/notes/') || route.path.startsWith('/home')
})
</script>

<template>
  <div class="min-h-screen bg-[#fafafa]">
    <topbar v-if="showTopbar" :active-view="route.path" />

    <router-view/>
  </div>
</template>