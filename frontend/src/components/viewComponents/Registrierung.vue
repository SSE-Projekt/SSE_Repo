<template>
  <div class="registration-container">
    <h2>Registrierung</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label for="reg-email">E-Mail (muss verifiziert werden):</label>
        <input
            id="reg-email"
            v-model="email"
            type="email"
            @blur="validateEmail"
            :class="{'input-error':emailError}"
            required
            placeholder="email@example.com"
        />
        <p v-if="emailError" class="error">{{ emailError }}</p>
      </div>

      <div class="form-group">
        <label for="reg-password">Passwort:</label>
        <input
            id="reg-password"
            v-model="password"
            type="password"
            @input="checkPasswordStrength"
            required
            placeholder="Passwort"
        />
        <p :class="strengthClass">Passwortstärke: {{ passwordStrengthText }}</p>
      </div>

      <div class="form-group">
        <label>Rolle wählen:</label>
        <select v-model="role">
          <option value="read">Lese-Rolle</option>
          <option value="write">Schreib-Rolle</option>
        </select>
      </div>

      <button type="submit" :disabled="!isPasswordSecure || !!emailError">Registrieren</button>

      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <p v-if="successMessage" class="success">{{ successMessage }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const email = ref('');
const emailError = ref('');
const password = ref('');
const role = ref('read');
const errorMessage = ref('');
const successMessage = ref('');

// Analyse simple de la force du mot de passe
const passwordStrengthText = ref('Zu kurz');
const isPasswordSecure = ref(false);

// Diese Variable hat im vorherigen Beispiel gefehlt:
const strengthClass = computed(() => {
  if (password.value.length === 0) return '';
  if (!isPasswordSecure.value) return 'text-danger'; // Rot für unsicher
  return 'text-success'; // Grün für sicher
});

const checkPasswordStrength = () => {
  const p = password.value;
  if (p.length < 8) {
    passwordStrengthText.value = 'Zu kurz (min 8 Zeichen)';
    isPasswordSecure.value = false;
  } else if (/[A-Z]/.test(p) && /[0-9]/.test(p) && /[^A-Za-z0-9]/.test(p)) {
    passwordStrengthText.value = 'Stark';
    isPasswordSecure.value = true;
  } else {
    passwordStrengthText.value = 'Mittel (fügen Sie Zahlen/Sonderzeichen hinzu)';
    isPasswordSecure.value = false;
  }
};

//format standard von email: name@domaim,extension
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const validateEmail = () => {
  if (!email.value) {
    emailError.value = "E-Mail ist erforderlich.";
  } else if (!emailRegex.test(email.value)) {
    emailError.value = "Ungültiges E-Mail-Format (z.B. name@beispiel.de).";
  } else {
    emailError.value = "";
  }
};


const handleRegister = async () => {
  try {
    validateEmail();
    if (emailError.value) return;
    //Simulierter API-Aufruf an Ihr lokales Backend
    console.log("Registrierung läuft für:", email.value, role.value);
    successMessage.value = "Registrierung erfolgreich! Bitte prüfen Sie Ihre E-Mails.";
    errorMessage.value = '';
  } catch (err) {
    errorMessage.value = "Registrierung fehlgeschlagen. Bitte versuchen Sie es erneut.";
  }
};
</script>

<style scoped>
.error { color: red; }
.success { color: green; }
.form-group { margin-bottom: 1rem; }
.text-danger { color: red; font-weight: bold; }
.text-success { color: green; font-weight: bold; }
.input-error { border: 2px solid red; }
</style>