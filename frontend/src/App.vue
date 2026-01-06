<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import TopBar from "@/components/viewComponents/topBar.vue";

const route = useRoute()

/** * ENTWICKLUNGSMETHODE: Manuelle Änderung der Rolle
 * Ändern „autor” in „leser”, um den Zugriff zu testen.
 */
// Zeige die Topbar nur auf den "App"-Seiten, nicht beim Login/Register
const showTopbar = computed(() => {
  return ['/notes', '/my-notes','/share-notes'].includes(route.path) || route.path.startsWith('/notes/')
})
</script>

<template>
  <div class="min-h-screen bg-[#fafafa]">
    <top-bar v-if="showTopbar" :active-view="route.path" :user-role="userRole"/>

    <router-view :user-role="userRole"/>
  </div>
</template>