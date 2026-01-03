<template>
  <div class="w-full max-w-md mx-auto">
    <div class="text-center mb-10 mt-10">
      <div class="inline-flex items-center justify-center bg-black text-white w-12 h-12 rounded-xl text-xl font-bold mb-4 shadow-lg">
        N
      </div>
      <h2 class="text-3xl font-bold text-gray-900">Willkommen zurück</h2>
      <p class="text-gray-500 mt-2">Bitte melde dich mit deinem Konto an.</p>
    </div>

    <div class="bg-white p-8 rounded-3xl border border-gray-200 shadow-sm">
      <form @submit.prevent="handleLogin" class="space-y-5">

        <div class="space-y-1.5">
          <label for="login-username" class="text-sm font-semibold text-gray-700 ml-1">Nutzername</label>
          <div class="relative">
            <span class="absolute inset-y-0 left-0 pl-3.5 flex items-center text-gray-400">
              <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current"><path :d="getIcon('account-outline')" /></svg>
            </span>
            <input
                id="login-username"
                v-model="username"
                type="text"
                required
                autocomplete="username"
                placeholder="email@example.com"
                class="w-full pl-11 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all text-gray-800 placeholder-gray-400"
            />
          </div>
        </div>

        <div class="space-y-1.5">
          <label for="login-password" class="text-sm font-semibold text-gray-700 ml-1">Passwort</label>
          <div class="relative">
            <span class="absolute inset-y-0 left-0 pl-3.5 flex items-center text-gray-400">
              <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current"><path :d="getIcon('lock-outline')" /></svg>
            </span>
            <input
                id="login-password"
                v-model="password"
                type="password"
                required
                autocomplete="current-password"
                placeholder="••••••••"
                class="w-full pl-11 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all text-gray-800 placeholder-gray-400"
            />
          </div>
        </div>

        <Transition name="fade">
          <div v-if="authError" class="flex items-center gap-2 p-3 bg-rose-50 border border-rose-100 rounded-xl text-rose-700 text-sm">
            <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current shrink-0"><path :d="getIcon('alert-circle-outline')" /></svg>
            <span>{{ authError }}</span>
          </div>
        </Transition>

        <button
            type="submit"
            :disabled="isLoading"
            class="w-full py-3.5 bg-black hover:bg-gray-800 text-white rounded-xl font-bold text-sm transition-all shadow-sm active:scale-[0.98] disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          <span v-if="!isLoading">Anmelden</span>
          <span v-else class="flex items-center justify-center gap-2">
            <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
              <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
              <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
            </svg>
            Wird geladen...
          </span>
        </button>
        <div class="text-center mt-6">
          <p class="text-sm text-gray-500">
            Noch kein Konto?
            <router-link to="/register" class="text-black font-semibold hover:underline underline-offset-4">
              Kostenlos registrieren
            </router-link>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { getIcon } from '@/utils/getIcon.js';

const emit = defineEmits(['login-success']);

const username = ref('');
const password = ref('');
const authError = ref('');
const isLoading = ref(false);

const handleLogin = async () => {
  authError.value = '';
  isLoading.value = true;

  try {
    // Simulierter Login-Prozess
    console.log("Login-Versuch für:", username.value);

    // Verzögerung für UX (Lade-Spinner zeigen)
    await new Promise(resolve => setTimeout(resolve, 1000));

    // Demo: Nur ein bestimmter User kommt durch
    if (username.value === 'admin' && password.value === 'password') {
      emit('login-success');
    } else {
      // Sicherheits-best-practice: Generische Fehlermeldung
      authError.value = "Anmeldung fehlgeschlagen. Bitte überprüfe deine Daten.";
    }
  } catch (err) {
    authError.value = "Ein technischer Fehler ist aufgetreten.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>