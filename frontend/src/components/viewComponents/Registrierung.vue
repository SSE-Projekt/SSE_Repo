<template>
  <div class="w-full max-w-lg mx-auto">
    <div class="text-center mb-10 mt-10">
      <div class="inline-flex items-center justify-center bg-black text-white w-12 h-12 rounded-xl text-xl font-bold mb-4 shadow-lg">
        N
      </div>
      <h2 class="text-3xl font-bold text-gray-900">Konto erstellen</h2>
      <p class="text-gray-500 mt-2">Werde Teil der Notes-Community.</p>
    </div>

    <div class="bg-white p-8 rounded-3xl border border-gray-200 shadow-sm">
      <form @submit.prevent="handleRegister" class="space-y-5">

        <div class="space-y-1.5">
          <label for="reg-email" class="text-sm font-semibold text-gray-700 ml-1">E-Mail Adresse</label>
          <div class="relative">
            <span class="absolute inset-y-0 left-0 pl-3.5 flex items-center text-gray-400">
              <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current"><path :d="getIcon('email-outline')" /></svg>
            </span>
            <input
                id="reg-email"
                v-model="email"
                type="email"
                @blur="validateEmail"
                required
                placeholder="name@beispiel.de"
                :class="[
                'w-full pl-11 pr-4 py-3 bg-gray-50 border rounded-xl outline-none transition-all text-gray-800 placeholder-gray-400',
                emailError ? 'border-rose-300 ring-1 ring-rose-100' : 'border-gray-200 focus:border-black focus:ring-1 focus:ring-black'
              ]"
            />
          </div>
          <p v-if="emailError" class="text-xs text-rose-600 ml-1 font-medium">{{ emailError }}</p>
        </div>

        <div class="space-y-1.5">
          <label for="reg-password" class="text-sm font-semibold text-gray-700 ml-1">Passwort</label>
          <div class="relative">
            <span class="absolute inset-y-0 left-0 pl-3.5 flex items-center text-gray-400">
              <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current"><path :d="getIcon('lock-outline')" /></svg>
            </span>
            <input
                id="reg-password"
                v-model="password"
                type="password"
                @input="checkPasswordStrength"
                required
                placeholder="Mindestens 8 Zeichen"
                class="w-full pl-11 pr-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all text-gray-800 placeholder-gray-400"
            />
          </div>

          <div v-if="password.length > 0" class="mt-2 space-y-2">
            <div class="flex gap-1 h-1.5">
              <div :class="['h-full flex-1 rounded-full transition-all duration-500', password.length < 8 ? 'bg-rose-400' : (isPasswordSecure ? 'bg-emerald-500' : 'bg-amber-400')]"></div>
              <div :class="['h-full flex-1 rounded-full transition-all duration-500', isPasswordSecure ? 'bg-emerald-500' : 'bg-gray-200']"></div>
              <div :class="['h-full flex-1 rounded-full transition-all duration-500', isPasswordSecure ? 'bg-emerald-500' : 'bg-gray-200']"></div>
            </div>
            <p :class="['text-xs font-medium', strengthClass]">
              Stärke: {{ passwordStrengthText }}
            </p>
          </div>
        </div>

        <div class="space-y-1.5">
          <label class="text-sm font-semibold text-gray-700 ml-1">Deine Rolle</label>
          <div class="grid grid-cols-2 gap-3">
            <button
                type="button"
                @click="role = 'read'"
                :class="['py-2.5 px-4 rounded-xl text-sm font-medium border transition-all', role === 'read' ? 'bg-black text-white border-black shadow-md' : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']"
            >
              Leser
            </button>
            <button
                type="button"
                @click="role = 'write'"
                :class="['py-2.5 px-4 rounded-xl text-sm font-medium border transition-all', role === 'write' ? 'bg-black text-white border-black shadow-md' : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']"
            >
              Autor
            </button>
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
            :disabled="!isPasswordSecure || !!emailError || !email"
            class="w-full py-3.5 bg-black hover:bg-gray-800 text-white rounded-xl font-bold text-sm transition-all shadow-sm active:scale-[0.98] disabled:bg-gray-200 disabled:text-gray-400 disabled:cursor-not-allowed mt-4"
        >
          Registrieren
        </button>

        <div class="text-center mt-6">
          <p class="text-sm text-gray-500">
            Bereits registriert?
            <router-link
                to="/login"
                class="text-black font-semibold hover:underline underline-offset-4 transition-all"
            >
              Jetzt anmelden
            </router-link>
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { getIcon } from '@/utils/getIcon';

const email = ref('');
const emailError = ref('');
const password = ref('');
const role = ref('read');
const errorMessage = ref('');
const successMessage = ref('');
const passwordStrengthText = ref('Zu kurz');
const isPasswordSecure = ref(false);

const strengthClass = computed(() => {
  if (password.value.length === 0) return '';
  if (!isPasswordSecure.value) return 'text-rose-600';
  return 'text-emerald-600';
});

const checkPasswordStrength = () => {
  const p = password.value;
  if (p.length < 8) {
    passwordStrengthText.value = 'Zu kurz (min 8 Zeichen)';
    isPasswordSecure.value = false;
  } else if (/[A-Z]/.test(p) && /[0-9]/.test(p) && /[^A-Za-z0-9]/.test(p)) {
    passwordStrengthText.value = 'Starkes Passwort';
    isPasswordSecure.value = true;
  } else {
    passwordStrengthText.value = 'Mittel (füge Zahlen/Sonderzeichen hinzu)';
    isPasswordSecure.value = false;
  }
};

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const validateEmail = () => {
  if (!email.value) {
    emailError.value = "E-Mail ist erforderlich.";
  } else if (!emailRegex.test(email.value)) {
    emailError.value = "Ungültiges E-Mail-Format.";
  } else {
    emailError.value = "";
  }
};

const handleRegister = async () => {
  try {
    validateEmail();
    if (emailError.value) return;
    successMessage.value = "Erfolg! Bitte prüfe deine E-Mails zur Verifizierung.";
    errorMessage.value = '';
  } catch (err) {
    errorMessage.value = "Registrierung fehlgeschlagen. Bitte erneut versuchen.";
  }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>