<template>
  <div class="login-container">
    <h2>Anmeldung</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="login-username">Nutzername:</label>
        <input
            id="login-username"
            v-model="username"
            type="text"
            required
            autocomplete="username"
            placeholder="email@example.com"
        />
      </div>

      <div class="form-group">
        <label for="login-password">Passwort:</label>
        <input
            id="login-password"
            v-model="password"
            type="password"
            required
            autocomplete="current-password"
            placeholder="Passwort"
        />
      </div>

      <button type="submit">Anmelden</button>

      <p v-if="authError" class="error-msg">{{ authError }}</p>
    </form>
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
    // Implementierung der Anmeldung mit Nutzername und Passwort
    console.log("Login-Versuch für:", username.value);

    // Schutz vor Angriffen: Keine spezifischen Fehler wie "Passwort falsch"
    // sondern generische Meldungen verwenden
    const success = false;
    if (!success) {
      authError.value = "Anmeldung fehlgeschlagen. Bitte überprüfen Sie Ihre Eingaben.";
    }
  } catch (err) {
    authError.value = "Ein technischer Fehler ist aufgetreten.";
  }
};
</script>

<style scoped>
.error-msg {
  color: #d9534f;
  margin-top: 10px;
}
.form-group {
  margin-bottom: 15px;
}
</style>