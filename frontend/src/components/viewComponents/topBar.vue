<template>
  <nav class="flex items-center justify-between px-8 py-4 bg-white border-b border-gray-100 sticky top-0 z-10">
    <div
        class="flex items-center gap-2 text-xl font-semibold cursor-pointer"
        @click="$emit('change-view', 'home')"
    >
      <div class="bg-black text-white w-8 h-8 flex items-center justify-center rounded-md text-sm font-bold">
        N
      </div>
      <span>Notes</span>
    </div>

    <div class="flex items-center gap-2 bg-gray-50 p-1 rounded-xl">
      <button
          @click="$emit('change-view', 'home')"
          :class="route.path === '/home'
          ? 'bg-black shadow-sm text-white font-medium'
          : 'text-gray-500 hover:text-black'"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all"
      >
        <router-link to="/home" class="nav-item flex items-center gap-2">
          <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current">
            <path :d="getIcon('home')" />
          </svg>
          Home
        </router-link>
      </button>

      <button
          v-if="user?.user_metadata.user_rolle === 2"
          @click="$emit('change-view', 'my-notes')"
          :class="route.path === '/my-notes'
          ? 'bg-black shadow-sm text-white font-medium'
          : 'text-gray-500 hover:text-black'"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all"
      >
        <router-link to="/my-notes" class="nav-item flex items-center gap-2">
          <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current">
            <path :d="getIcon('file-document-outline')" />
          </svg>
          My Notes
        </router-link>
      </button>

      <button
          @click="$emit('change-view', 'share-notes')"
          :class="route.path === '/share-notes'
          ? 'bg-black shadow-sm text-white font-medium'
          : 'text-gray-500 hover:text-black'"
          class="flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all"
      >
        <router-link to="/share-notes" class="nav-item flex items-center gap-2">
          <svg viewBox="0 0 24 24" class="w-[18px] h-[18px] fill-current">
            <path :d="getIcon('share-variant')" />
          </svg>
          Shared Notes
        </router-link>
      </button>
    </div>

    <!-- Right side -->
    <div class="flex items-center gap-3 relative">
      <!-- Account + Dropdown -->
      <div class="relative" ref="dropdownRef">
        <div
            class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center text-gray-400 cursor-pointer hover:bg-gray-200 transition"
            @click="toggleDropdown"
        >
          <svg viewBox="0 0 24 24" class="w-6 h-6 fill-current">
            <path :d="getIcon('account')" />
          </svg>
        </div>

        <!-- Dropdown -->
        <div
            v-if="isDropdownOpen"
            class="absolute right-0 mt-2 w-56 bg-white border border-gray-200 rounded-xl shadow-lg p-4 z-50"
        >
          <p class="text-sm font-medium text-gray-900 truncate">
            {{ user?.user_metadata.user_name || 'Unbekannter Nutzer' }}
          </p>
          <p class="text-sm text-gray-500 truncate">
            {{ user?.email }}
          </p>

          <div class="mt-3 border-t pt-3">
            <button
                @click="handleLogout"
                class="w-full flex items-center justify-between text-sm text-rose-600 hover:bg-rose-50 px-3 py-2 rounded-lg transition"
            >
              <span>Abmelden</span>

              <svg viewBox="0 0 24 24" class="w-5 h-5 fill-current">
                <path :d="getIcon('logout')" />
              </svg>
            </button>
          </div>

        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import {useRouter, useRoute} from 'vue-router'
import {getIcon} from '@/utils/getIcon'
import {ref, onMounted, onBeforeUnmount} from 'vue'

const props = defineProps({
  activeView: String,
})

defineEmits(['change-view'])

const router = useRouter()
const route = useRoute()
const isDropdownOpen = ref(false)
const dropdownRef = ref(null)

const handleLogout = async () => {
  localStorage.removeItem('user')
  await router.push('/login')
}

const toggleDropdown = (event) => {
  event.stopPropagation()
  isDropdownOpen.value = !isDropdownOpen.value
}

const handleClickOutside = (event) => {
  if (!dropdownRef.value?.contains(event.target)) {
    isDropdownOpen.value = false
  }
}
const user = ref(null)
onMounted(() => {
  const storedUser = localStorage.getItem('user')
  user.value = storedUser ? JSON.parse(storedUser) : null

  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>


