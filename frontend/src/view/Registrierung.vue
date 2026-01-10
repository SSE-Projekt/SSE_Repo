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

          <ul class="password-requirements mt-2 text-xs space-y-1 text-gray-500">
            <li :class="requirements.forbidden ? 'text-gray-500' : 'text-rose-600 font-bold'">
              {{ requirements.forbidden ? '✔ Keine verbotenen Symbole' : '✘ Symbole < > \' " ( ) - und Leerzeichen sind verboten' }}
            </li>
            <li :class="{ 'text-emerald-600 font-medium': requirements.length }">
              {{ requirements.length ? '✔' : '○' }} Mindestens 8 Zeichen
            </li>
            <li :class="{ 'text-emerald-600 font-medium': requirements.uppercase }">
              {{ requirements.uppercase ? '✔' : '○' }} Ein Großbuchstabe (A-Z)
            </li>
            <li :class="{ 'text-emerald-600 font-medium': requirements.lowercase }">
              {{ requirements.lowercase ? '✔' : '○' }} Ein Kleinbuchstabe (a-z)
            </li>
            <li :class="{ 'text-emerald-600 font-medium': requirements.number }">
              {{ requirements.number ? '✔' : '○' }} Eine Zahl (0-9)
            </li>
            <li :class="{ 'text-emerald-600 font-medium': requirements.special }">
              {{ requirements.special ? '✔' : '○' }} Ein Sonderzeichen (!@#$...)
            </li>
          </ul>
        </div>

        <div class="space-y-1.5">
          <label class="text-sm font-semibold text-gray-700 ml-1">Deine Rolle</label>
          <div class="grid grid-cols-2 gap-3">
            <button
                type="button"
                @click="role = 1; agreement = false"
                :class="['py-2.5 px-4 rounded-xl text-sm font-medium border transition-all', role === 1 ? 'bg-black text-white border-black shadow-md' : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']"
            >
              Leser
            </button>
            <button
                type="button"
                @click="role = 2; agreement = false"
                :class="['py-2.5 px-4 rounded-xl text-sm font-medium border transition-all', role === 2 ? 'bg-black text-white border-black shadow-md' : 'bg-white text-gray-600 border-gray-200 hover:bg-gray-50']"
            >
              Autor
            </button>
          </div>

          <div class="mt-3 p-3 bg-gray-50 rounded-xl border border-gray-100 text-xs text-gray-600 leading-relaxed">
            <p v-if="role === 1"><strong>Leser:</strong> Du kannst öffentliche und dir geteilte Notizen lesen, aber keine neuen globalen Inhalte veröffentlichen.</p>
            <p v-else><strong>Autor:</strong> Du hast volle Rechte zum Erstellen und Teilen privaten Notizen und  Veröffentlichen von öffentlichen Notizen für die Community.</p>
          </div>

          <div class="flex items-center gap-2 mt-4 px-1">
            <input
                type="checkbox"
                id="agreement"
                v-model="agreement"
                class="w-4 h-4 accent-black cursor-pointer"
            >
            <label for="agreement" class="text-xs text-gray-600 cursor-pointer">
              Ich bin mit den Berechtigungen der Rolle <strong>{{ role === 'read' ? 'Leser' : 'Autor' }}</strong> einverstanden.
            </label>
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
            :disabled="!isPasswordSecure || emailError || !email || !agreement"
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
import { getIcon } from '@/utils/getIcon.js';
import {useRouter} from "vue-router";
import axios from 'axios';
import DOMPurify from 'dompurify'; // Import für XSS Schutz


const router = useRouter()

const email = ref('');
const emailError = ref(null);
const password = ref('');
const role = ref(0);
const agreement = ref(false); // État pour la case à cocher
const errorMessage = ref(null);
const successMessage = ref(null);

const requirements = ref({
  length: false,
  uppercase: false,
  lowercase: false,
  number: false,
  special: false,
  forbidden: true
});

const isPasswordSecure = ref(false);

const checkPasswordStrength = () => {
  const p = password.value;
  const hasForbidden = /[<>'"()\- ]/.test(p);
  requirements.value.forbidden = !hasForbidden;
  requirements.value.length = p.length >= 8;
  requirements.value.uppercase = /[A-Z]/.test(p);
  requirements.value.lowercase = /[a-z]/.test(p);
  requirements.value.number = /[0-9]/.test(p);
  requirements.value.special = /[^A-Za-z0-9]/.test(p);

  isPasswordSecure.value = Object.values(requirements.value).every(v => v === true);
};

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const validateEmail = () => {
  const rawValue = email.value;
  const sanitizedValue = DOMPurify.sanitize(rawValue);

  // Injektionserkennung: Wenn beide unterschiedlich sind, liegt ein Problem vor.
  if (rawValue !== sanitizedValue) {
    emailError.value = "Sicherheitsrisiko erkannt: Ungültige Zeichen im Feld.";
    return; // Wir hören auf, wir testen nicht einmal die Regex.
  }

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
    if (emailError.value || !agreement.value) return;
    const response = await axios.post('http://localhost:8080/api/auth/signup', {
      email: email.value,
      password: password.value,
      metadata: {
        user_rolle: role.value
      }
    });
    console.log("Erfolgreich registriert:", response.data);
    successMessage.value = "Erfolg! Bitte prüfe deine E-Mails zur Verifizierung." + "\n Du findest darin deinen Username. Merke dir ihn für die Anmeldung";
    errorMessage.value = null;
    await router.push('/login')

  } catch (err) {
    errorMessage.value = "Fehler bei der Registrierung: email existiert bereits in Secure Note";
    successMessage.value = null
    email.value = null;
    password.value = null;
    role.value = 0
  }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>