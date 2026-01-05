<template>
  <div class="min-h-screen flex items-center justify-center bg-[#fafafa] px-4">
    <div class="text-center max-w-md">
      <div class="inline-flex items-center justify-center bg-rose-100 text-rose-600 w-20 h-20 rounded-full mb-6">
        <svg viewBox="0 0 24 24" class="w-10 h-10 fill-current">
          <path d="M12,2L1,21H23L12,2M12,6L19.53,19H4.47L12,6M11,10V14H13V10H11M11,16V18H13V16H11Z" />
        </svg>
      </div>
      <h1 class="text-4xl font-bold text-gray-900 mb-2">403</h1>
      <h2 class="text-xl font-semibold text-gray-800 mb-4">Zugriff verweigert</h2>
      <p class="text-gray-500 mb-8">
        Du hast nicht die erforderlichen Berechtigungen (Autor-Status), um auf diese Seite zuzugreifen.
      </p>
      <router-link
          to="/home"
          class="inline-block bg-black text-white px-8 py-3 rounded-xl font-bold hover:bg-gray-800 transition-all shadow-sm"
      >
        Zurück zur Startseite
      </router-link>
    </div>
  </div>
</template>