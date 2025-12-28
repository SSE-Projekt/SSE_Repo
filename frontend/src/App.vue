<script setup>
import { ref } from 'vue'
import Anmeldung from './components/viewComponents/Anmeldung.vue'
import Registrierung from './components/viewComponents/Registrierung.vue'

// Zustand, um zu steuern, was angezeigt wird: 'home', 'login' oder 'register'
const view = ref('home')

const setView = (newView) => {
  view.value = newView
}
</script>

<template>
  <header>
    <img alt="Vue logo" class="logo" src="./assets/logo.svg" width="125" height="125" />

    <div class="wrapper">
      <div v-if="view === 'home'" class="nav-buttons">
        <h1>Willkommen</h1>
        <p>Bitte wählen Sie eine Option:</p>
        <button @click="setView('login')">Anmeldung</button>
        <button @click="setView('register')" class="btn-secondary">Registrierung</button>
      </div>

      <div v-if="view === 'login'">
        <Anmeldung />
        <button class="link-btn" @click="setView('register')">Noch kein Konto? Registrieren</button>
        <br />
        <button class="back-btn" @click="setView('home')"> Zurück</button>
      </div>

      <div v-if="view === 'register'">
        <Registrierung />
        <button class="link-btn" @click="setView('login')">Bereits ein Konto? Anmelden</button>
        <br />
        <button class="back-btn" @click="setView('home')"> Zurück</button>
      </div>
    </div>
  </header>

  <main>
  </main>
</template>

<style scoped>
header {
  line-height: 1.5;
  text-align: center;
}

.logo {
  display: block;
  margin: 0 auto 2rem;
}

.nav-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

button {
  padding: 0.8rem 2rem;
  font-size: 1rem;
  cursor: pointer;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
}

.btn-secondary {
  background-color: #35495e;
}

.link-btn {
  background: none;
  border: none;
  color: #42b883;
  text-decoration: underline;
  padding: 0;
  margin-top: 1rem;
  font-size: 0.9rem;
}

.back-btn {
  background-color: #ccc;
  color: #333;
  margin-top: 2rem;
  padding: 0.5rem 1rem;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: calc(var(--section-gap) / 2);
  }

  .logo {
    margin: 0 2rem 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
    text-align: left;
  }
}
</style>