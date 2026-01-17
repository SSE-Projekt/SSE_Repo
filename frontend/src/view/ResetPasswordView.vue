<template>
  <div class="min-h-screen bg-[#fafafa] flex items-center justify-center p-4">
    <div class="bg-white w-full max-w-lg rounded-[2.5rem] p-10 shadow-sm border border-gray-100">

      <div class="text-center mb-8">
        <div class="inline-flex items-center justify-center bg-black text-white w-12 h-12 rounded-xl text-xl font-bold mb-4 shadow-lg">
          N
        </div>
        <h2 class="text-2xl font-bold text-gray-900">Neues Passwort festlegen</h2>
        <p class="text-sm text-gray-500 mt-2">Konto: <span class="font-semibold text-gray-800">{{ email }}</span></p>
      </div>

      <form @submit.prevent="handleReset" class="space-y-6">
        <div class="space-y-1.5">
          <label class="text-sm font-semibold text-gray-700 ml-1">Neues Passwort</label>
          <input
              v-model="password"
              type="password"
              @input="checkPasswordStrength"
              required
              placeholder="••••••••"
              class="w-full px-4 py-3.5 bg-gray-50 border border-gray-200 rounded-2xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all"
          />
        </div>

        <div class="space-y-1.5">
          <label class="text-sm font-semibold text-gray-700 ml-1">Passwort bestätigen</label>
          <input
              v-model="confirmPassword"
              type="password"
              required
              placeholder="••••••••"
              :class="[
                'w-full px-4 py-3.5 bg-gray-50 border rounded-2xl outline-none transition-all',
                !passwordsMatch && confirmPassword ? 'border-rose-300 ring-rose-100 ring-1' : 'border-gray-200 focus:border-black focus:ring-1 focus:ring-black'
              ]"
          />
          <p v-if="!passwordsMatch && confirmPassword" class="text-[11px] text-rose-600 ml-1 font-medium italic">
            Passwörter stimmen nicht überein.
          </p>
        </div>

        <ul class="p-4 bg-gray-50 rounded-2xl space-y-2 text-[11px]">
          <li :class="requirements.forbidden ? 'text-gray-400' : 'text-rose-600 font-bold'">
            {{ requirements.forbidden ? '✔ Keine verbotenen Symbole' : '✘ Verboten: < > \' " ( ) - oder Leerzeichen' }}
          </li>
          <li :class="requirements.length ? 'text-emerald-600 font-medium' : 'text-gray-400'">
            {{ requirements.length ? '✔' : '○' }} Mindestens 8 Zeichen
          </li>
          <li :class="requirements.uppercase ? 'text-emerald-600 font-medium' : 'text-gray-400'">
            {{ requirements.uppercase ? '✔' : '○' }} Ein Großbuchstabe
          </li>
          <li :class="requirements.uppercase ? 'text-emerald-600 font-medium' : 'text-gray-400'">
            {{ requirements.number ? '✔' : '○' }} Ein Ziffer
          </li>
          <li :class="requirements.special ? 'text-emerald-600 font-medium' : 'text-gray-400'">
            {{ requirements.special ? '✔' : '○' }} Ein Sonderzeichen
          </li>
        </ul>

        <button
            type="submit"
            :disabled="!isFormValid || isLoading"
            class="w-full py-4 bg-black text-white rounded-2xl font-bold text-sm hover:bg-gray-800 transition-all shadow-lg active:scale-[0.98] disabled:bg-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed"
        >
          <span v-if="!isLoading">Passwort aktualisieren</span>
          <span v-else class="flex items-center justify-center gap-2">Wird geladen...</span>
        </button>
      </form>
    </div>

    <SnackBar v-model:show="snackbar.show" :message="snackbar.message" :type="snackbar.type" />
  </div>
</template>

<script setup>
import { ref, computed, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';
import DOMPurify from 'dompurify';
import SnackBar from "@/components/viewComponents/snackBar.vue";

const route = useRoute();
const router = useRouter();

const email = ref('');
const token = ref('');
const password = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);

const snackbar = reactive({ show: false, message: '', type: 'success' });

const requirements = ref({
  length: false, uppercase: false, lowercase: false,
  number: false, special: false, forbidden: true
});

onMounted(() => {
  email.value = route.query.email || '';
  token.value = route.query.token || '';

  if (!token.value) {
    handleError("Ungültiger oder fehlender Sicherheits-Token.");
  }
});

const checkPasswordStrength = () => {
  const p = password.value;
  const hasForbidden = /[<>'"()\- ]/.test(p);
  requirements.value.forbidden = !hasForbidden;
  requirements.value.length = p.length >= 8;
  requirements.value.uppercase = /[A-Z]/.test(p);
  requirements.value.lowercase = /[a-z]/.test(p);
  requirements.value.number = /[0-9]/.test(p);
  requirements.value.special = /[^A-Za-z0-9]/.test(p);
};

const passwordsMatch = computed(() => password.value === confirmPassword.value);

const isFormValid = computed(() => {
  return Object.values(requirements.value).every(v => v === true) &&
      passwordsMatch.value &&
      password.value.length > 0;
});

const handleSuccess = (msg) => {
  snackbar.message = msg;
  snackbar.type = 'success';
  snackbar.show = true;
};

const handleError = (msg) => {
  snackbar.message = msg;
  snackbar.type = 'failed';
  snackbar.show = true;
};

const handleReset = async () => {
  if (password.value !== DOMPurify.sanitize(password.value)) {
    handleError("Sicherheitsproblem: Ungültige Zeichen im Passwort.");
    return;
  }

  isLoading.value = true;
  try {
    await axios.post('http://localhost:8080/api/auth/reset-password', {
      token: token.value,
      email: email.value,
      newPassword: password.value
    });

    handleSuccess("Passwort erfolgreich geändert! Du wirst zum Login weitergeleitet.");

    setTimeout(() => {
      router.push('/login');
    }, 2000);
  } catch (err) {
    handleError("Fehler beim Zurücksetzen du Passwords. Der Link ist eventuell abgelaufen.");
  } finally {
    isLoading.value = false;
  }
};
</script>