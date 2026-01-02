<template>
  <div class="login-box">
    <h2>Anmeldung</h2>
    <p class="subtitle">Willkommen zurück! Bitte melden Sie sich an.</p>

    <form @submit.prevent="handleLogin" class="form-content">
      <div class="form-group">
        <label for="login-username">Nutzername / E-Mail</label>
        <input
            id="login-username"
            v-model="username"
            type="text"
            required
            autocomplete="username"
            placeholder="name@beispiel.de"
        />
      </div>

      <div class="form-group">
        <label for="login-password">Passwort</label>
        <input
            id="login-password"
            v-model="password"
            type="password"
            required
            autocomplete="current-password"
            placeholder="Ihr Passwort"
        />
      </div>

      <button type="submit" class="btn-submit">Anmelden</button>

      <transition name="fade">
        <p v-if="authError" class="error-msg">{{ authError }}</p>
      </transition>
    </form>

    <div class="footer-links">
      <router-link to="/register" class="link-btn">Noch kein Konto? Registrieren</router-link>
      <router-link to="/" class="back-link">← Zurück zur Startseite</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const username = ref('');
const password = ref('');
const authError = ref('');

const handleLogin = async () => {
  authError.value = '';
  try {
    console.log("Login-Versuch für:", username.value);

    // Simuler un échec pour montrer le style de l'erreur
    const success = false;
    if (!success) {
      // Message générique recommandé par l'OWASP
      authError.value = "Anmeldung fehlgeschlagen. Nutzername oder Passwort ist falsch.";
    }
  } catch (err) {
    authError.value = "Ein technischer Fehler ist aufgetreten.";
  }
};
</script>

<style scoped>
.login-box {
  width: 100%;
}

h2 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.8rem;
}

.subtitle {
  color: #666;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

.form-content {
  text-align: left; /* Aligne les labels à gauche pour une meilleure lecture */
}

.form-group {
  margin-bottom: 1.2rem;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
  color: #34495e;
}

input {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
}

input:focus {
  outline: none;
  border-color: #42b883;
  box-shadow: 0 0 0 3px rgba(66, 184, 131, 0.1);
}

.btn-submit {
  width: 100%;
  padding: 0.8rem;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
  transition: background 0.2s;
}

.btn-submit:hover {
  background-color: #3aa876;
}

.error-msg {
  background-color: #fdeaea;
  color: #d9534f;
  padding: 0.75rem;
  border-radius: 6px;
  margin-top: 1rem;
  font-size: 0.85rem;
  border: 1px solid #f5c6cb;
}

.footer-links {
  margin-top: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
  border-top: 1px solid #eee;
  padding-top: 1.5rem;
}

.link-btn {
  color: #42b883;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
}

.link-btn:hover {
  text-decoration: underline;
}

.back-link {
  color: #95a5a6;
  text-decoration: none;
  font-size: 0.85rem;
}

/* Animation simple pour l'erreur */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>