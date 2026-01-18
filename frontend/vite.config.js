import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'

export default defineConfig(({ mode }) => ({
  plugins: [
    vue(),
    // DevTools nur laden, wenn wir nicht im Test-Modus sind
    mode !== 'test' && VueDevTools(),
  ],
  test: {
    environment: 'jsdom', //  Wichtig für Vue-Komponenten
    globals: true,
  },
}))