<template>
  <div class="registration-box">
    <h2>Registrierung</h2>
    <p class="subtitle">Erstellen Sie ein sicheres Konto.</p>

    <form @submit.prevent="handleRegister" class="form-content">
      <div class="form-group">
        <label for="reg-email">E-Mail Adresse</label>
        <input
            id="reg-email"
            v-model="email"
            type="email"
            @blur="validateEmail"
            :class="{'input-error': emailError}"
            required
            placeholder="name@beispiel.de"
        />
        <p v-if="emailError" class="error-text">{{ emailError }}</p>
      </div>

      <div class="form-group">
        <label for="reg-password">Passwort</label>
        <input
            id="reg-password"
            v-model="password"
            type="password"
            @input="checkPasswordStrength"
            :class="{'input-success': isPasswordSecure && password.length > 0}"
            required
            placeholder="Sicheres Passwort wählen"
        />

        <ul class="password-requirements">
          <li :class="{ 'met': requirements.length }">
            <span class="icon">{{ requirements.length ? '✔' : '○' }}</span>
            Mindestens 8 Zeichen
          </li>
          <li :class="{ 'met': requirements.uppercase }">
            <span class="icon">{{ requirements.uppercase ? '✔' : '○' }}</span>
            Ein Großbuchstabe (A-Z)
          </li>
          <li :class="{ 'met': requirements.lowercase }">
            <span class="icon">{{ requirements.lowercase ? '✔' : '○' }}</span>
            Ein Kleinbuchstabe (a-z)
          </li>
          <li :class="{ 'met': requirements.number }">
            <span class="icon">{{ requirements.number ? '✔' : '○' }}</span>
            Eine Zahl (0-9)
          </li>
          <li :class="{ 'met': requirements.special }">
            <span class="icon">{{ requirements.special ? '✔' : '○' }}</span>
            Ein Sonderzeichen (!@#$...)
          </li>
        </ul>
      </div>

      <div class="form-group">
        <label for="reg-role">Rolle wählen</label>
        <select id="reg-role" v-model="role" class="select-input">
          <option value="read">Lese-Rolle</option>
          <option value="write">Schreib-Rolle</option>
        </select>
      </div>

      <button
          type="submit"
          class="btn-submit"
          :disabled="!isPasswordSecure || !!emailError"
      >
        Konto erstellen
      </button>

      <transition name="fade">
        <div v-if="errorMessage" class="msg error-box">{{ errorMessage }}</div>
        <div v-else-if="successMessage" class="msg success-box">{{ successMessage }}</div>
      </transition>
    </form>

    <div class="footer-links">
      <router-link to="/login" class="link-btn">Bereits ein Konto? Anmelden</router-link>
      <router-link to="/" class="back-link">← Zurück zur Startseite</router-link>
    </div>
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

// État des exigences du mot de passe
const requirements = ref({
  length: false,
  uppercase: false,
  lowercase: false,
  number: false,
  special: false
});

const isPasswordSecure = ref(false);

const checkPasswordStrength = () => {
  const p = password.value;

  // Tests individuels
  requirements.value.length = p.length >= 8;
  requirements.value.uppercase = /[A-Z]/.test(p);
  requirements.value.lowercase = /[a-z]/.test(p);
  requirements.value.number = /[0-9]/.test(p);
  requirements.value.special = /[^A-Za-z0-9]/.test(p);

  // Validation globale : toutes les conditions doivent être vraies
  isPasswordSecure.value = Object.values(requirements.value).every(v => v === true);
};

// Validation E-mail
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
  errorMessage.value = '';
  successMessage.value = '';

  try {
    validateEmail();
    if (emailError.value || !isPasswordSecure.value) return;

    console.log("Inscription de :", email.value, "Rôle :", role.value);
    // Ici, vous ferez votre appel fetch() vers votre serveur local
    successMessage.value = "Registrierung erfolgreich! Bitte prüfen Sie Ihre E-Mails.";
  } catch (err) {
    errorMessage.value = "Ein Fehler ist aufgetreten.";
  }
};
</script>

<style scoped>
.registration-box { width: 100%; }
h2 { margin: 0; color: #2c3e50; font-size: 1.8rem; }
.subtitle { color: #666; margin-bottom: 1.5rem; font-size: 0.9rem; }

.form-content { text-align: left; }
.form-group { margin-bottom: 1.2rem; display: flex; flex-direction: column; }

label { font-weight: 600; margin-bottom: 0.4rem; font-size: 0.9rem; color: #34495e; }

input, .select-input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: all 0.2s;
}

input:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 3px rgba(66, 184, 131, 0.1);
}

.input-error { border-color: #d9534f; }
.input-success { border-color: #2ecc71; }

.error-text { color: #d9534f; font-size: 0.8rem; margin-top: 0.3rem; }

/* Styles pour les exigences du mot de passe */
.password-requirements {
  list-style: none;
  padding: 0;
  margin: 0.8rem 0;
  font-size: 0.85rem;
}

.password-requirements li {
  color: #95a5a6;
  margin-bottom: 0.3rem;
  display: flex;
  align-items: center;
  transition: color 0.3s ease;
}

.password-requirements li.met {
  color: #2ecc71;
  font-weight: 500;
}

.icon { margin-right: 10px; font-family: monospace; }

.btn-submit {
  width: 100%;
  padding: 0.8rem;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 1rem;
}

.btn-submit:disabled {
  background-color: #a8d5c2;
  cursor: not-allowed;
}

.msg { padding: 0.75rem; border-radius: 6px; margin-top: 1rem; font-size: 0.85rem; }
.error-box { background-color: #fdeaea; color: #d9534f; border: 1px solid #f5c6cb; }
.success-box { background-color: #eafaf1; color: #2ecc71; border: 1px solid #d4efdf; }

.footer-links {
  margin-top: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  border-top: 1px solid #eee;
  padding-top: 1.5rem;
  text-align: center;
}

.link-btn { color: #42b883; text-decoration: none; font-weight: 500; }
.back-link { color: #95a5a6; text-decoration: none; font-size: 0.85rem; }

.fade-enter-active { transition: opacity 0.5s; }
.fade-enter-from { opacity: 0; }
</style>