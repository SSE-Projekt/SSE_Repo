<template>
  <Transition name="fade">
    <div v-if="modelValue" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
      <div class="bg-white w-full max-w-md rounded-3xl p-8 shadow-2xl border border-gray-100">

        <div class="flex justify-between items-center mb-6">
          <h3 class="text-xl font-bold text-gray-900">Passwort vergessen?</h3>
          <button @click="close" class="text-gray-400 hover:text-black transition-colors">
            <svg viewBox="0 0 24 24" class="w-6 h-6 fill-current">
              <path d="M19,6.41L17.59,5L12,10.59L6.41,5L5,6.41L10.59,12L5,17.59L6.41,19L12,13.41L17.59,19L19,17.59L13.41,12L19,6.41Z" />
            </svg>
          </button>
        </div>

        <p class="text-sm text-gray-500 mb-6">
          Gib deine E-Mail-Adresse ein. Wir senden dir einen Link, um dein Passwort zurückzusetzen.
        </p>

        <form @submit.prevent="submitRequest" class="space-y-4">
          <div class="space-y-1.5">
            <label class="text-xs font-semibold text-gray-700 ml-1">E-Mail Adresse</label>
            <input
                v-model="resetEmail"
                type="email"
                required
                placeholder="name@beispiel.de"
                class="w-full px-4 py-3 bg-gray-50 border border-gray-200 rounded-xl outline-none focus:border-black focus:ring-1 focus:ring-black transition-all"
            />
          </div>

          <button
              type="submit"
              :disabled="isLoading"
              class="w-full py-3 bg-black text-white rounded-xl font-bold text-sm hover:bg-gray-800 transition-all disabled:bg-gray-300"
          >
            {{ isLoading ? 'Wird gesendet...' : 'Link senden' }}
          </button>
        </form>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import DOMPurify from 'dompurify';

const props = defineProps({
  modelValue: Boolean, // Für das v-Modell des übergeordneten Elements
  initialEmail: String // Vorausfüllen, wenn bereits eingegeben
});

const emit = defineEmits(['update:modelValue', 'success', 'error', 'warn']);

const resetEmail = ref(props.initialEmail || '');
const isLoading = ref(false);

const close = () => emit('update:modelValue', false);

const submitRequest = async () => {
  if (!resetEmail.value) return;

  // Sécurité XSS
  if (resetEmail.value !== DOMPurify.sanitize(resetEmail.value)) {
    emit('warn', "Sicherheitsrisiko erkannt.");
    return;
  }

  isLoading.value = true;
  try {
    await axios.post('http://localhost:8080/api/auth/forgot', {
      email: resetEmail.value
    });

    emit('success', "E-Mail wurde gesendet!");
    close();
  } catch (err) {
    emit('error', `Fehler beim Senden: ${err}.`);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.fade-enter-active, .fade-leave-active { transition: opacity 0.3s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>