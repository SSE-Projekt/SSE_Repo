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
          <div v-if="errorMessage || successMessage"
               :class="['flex items-center gap-2 p-3 rounded-xl text-sm border', errorMessage ? 'bg-rose-50 border-rose-100 text-rose-700' : 'bg-emerald-50 border-emerald-100 text-emerald-700']">
            <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current shrink-0">
              <path :d="getIcon(errorMessage ? 'alert-circle-outline' : 'check-circle-outline')" />
            </svg>
            <span>{{ errorMessage || successMessage }}</span>
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
    <SnackBar
        v-model:show="snackbar.show"
        :message="snackbar.message"
        :type="snackbar.type"
    />
  </div>
</template>

<script setup>
import {reactive, ref, watch} from 'vue';
import { getIcon } from '@/utils/getIcon.js';
import {useRouter} from 'vue-router';
import axios from 'axios';
import DOMPurify from 'dompurify';
import SnackBar from "@/components/viewComponents/snackBar.vue";


const emit = defineEmits(['login-success']);

const username = ref('');
const password = ref('');
const isLoading = ref(false);
const errorMessage = ref(null);
const successMessage = ref(null);

const router = useRouter()

const snackbar = reactive({
  show: false,
  message: '',
  type: 'success'
});

const handleSuccess = (msg) => {
  snackbar.message = msg || 'Erfolgreich angemeldet!';
  snackbar.type = 'success';
  snackbar.show = true;
};

const handleWarn = (msg) => {
  snackbar.message = msg || 'Sicherheitswarnung!';
  snackbar.type = 'warn';
  snackbar.show = true;
};

const handleError = (msg) => {
  snackbar.message = msg || 'Fehler aufgetreten!';
  snackbar.type = 'failed';
  snackbar.show = true;
};

const handleLogin = async () => {
  errorMessage.value = null;
  successMessage.value = null;

  if (username.value !== DOMPurify.sanitize(username.value)) {
    handleWarn("Sicherheitsproblem: Verbotene Zeichen erkannt.");
    return; // Der Anmeldevorgang wird blockiert.
  }

  isLoading.value = true;

  try {
    // Simulierter Login-Prozess
    console.log("Login-Versuch für:", username.value);

    // Verzögerung für UX (Lade-Spinner zeigen)
    await new Promise(resolve => setTimeout(resolve, 1000));

    const response = await axios.post('http://localhost:8080/api/auth/login', {
      login: username.value,
      password: password.value
    });

    // 1. JWT Token speichern (für API-Anfragen)
    const token = response.data.access_token;
    localStorage.setItem('token', token);

    // 2. Das komplette User-Objekt speichern
    // response.data.user enthält die ID, Email und die Metadata (Username, Rolle)
    const user = response.data.user;
    localStorage.setItem('user', JSON.stringify(user));

    successMessage.value = "Erfolg! Du bist in Secure Note angemeldet";
    handleSuccess("Willkommen zurück!");
    setTimeout(() => {
      router.push('/home');
    }, 1000);

  } catch (err) {
    errorMessage.value = "\"Anmeldung fehlgeschlagen. Bitte überprüfe deine Daten.\"";
    successMessage.value = null
  } finally {
    isLoading.value = false;
  }
};

const immediateLogin = async () => {
  const userRole = ref('autor');
// Sofortige Synchronisierung mit localStorage für den Router
// Dadurch muss index.js nicht manuell geändert werden.
  watch(userRole, (newRole) => {
    localStorage.setItem('user', JSON.stringify({role: newRole}));
  }, {immediate: true});

  router.push('/home');

}

</script>

<style scoped>
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>