<template>
  <div class="flex items-center justify-center py-12">
    <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
    <span class="ml-3 text-gray-500 dark:text-gray-400">{{ message }}</span>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: 'Loading...' }
})
</script>
