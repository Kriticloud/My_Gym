<template>
  <div :class="{ 'dark': isDark }" class="min-h-screen">
    <router-view />
  </div>
</template>

<script setup>
import { ref, provide } from 'vue'

const isDark = ref(localStorage.getItem('darkMode') === 'true')

function toggleDark() {
  isDark.value = !isDark.value
  localStorage.setItem('darkMode', isDark.value)
}

provide('isDark', isDark)
provide('toggleDark', toggleDark)
</script>
