import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import VueDevTools from 'vite-plugin-vue-devtools'
import path from 'path' // Importiert das Pfad-Modul von Node.js

export default defineConfig(({ mode }) => ({
  plugins: [
    vue(),
    // DevTools nur laden, wenn wir nicht im Test-Modus sind
    mode !== 'test' && VueDevTools(),
  ],
  resolve: {
    alias: {
      // Definiert '@' als Alias für den 'src'-Ordner
      '@': path.resolve(__dirname, './src'),
    },
  },
  test: {
    environment: 'jsdom', // Wichtig für Vue-Komponenten
    globals: true,
  },
}))