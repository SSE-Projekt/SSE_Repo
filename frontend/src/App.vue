<script setup>
import {computed, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import TopBar from "@/components/viewComponents/topBar.vue";

const route = useRoute()
const router = useRouter()

const user = JSON.parse(localStorage.getItem('user'))?? null
const userRole = computed(() => user? user.user_metadata.user_rolle : 0)
// Zeige die Topbar nur auf den "App"-Seiten, nicht beim Login/Register
const showTopbar = computed(() => {
  return ['/notes', '/my-notes','/share-notes'].includes(route.path) || route.path.startsWith('/notes/') || route.path.startsWith('/home')
})

onMounted(() => {
  // Extrahiert den Token aus der URL (z.B. #access_token=xyz...)
  const hash = window.location.hash;
  if (hash) {
    const params = new URLSearchParams(hash.replace('#', '?'));
    const token = params.get('access_token');
    localStorage.setItem('supabase', token);
    if(token) router.push('/reset-password')
  }
});
</script>

<template>
  <div class="min-h-screen bg-[#fafafa]">
    <top-bar v-if="showTopbar" :active-view="route.path" />

    <router-view/>
  </div>
</template>