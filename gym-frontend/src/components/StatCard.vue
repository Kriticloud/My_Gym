<template>
  <div class="card p-6">
    <div class="flex items-center justify-between">
      <div>
        <p class="text-sm font-medium text-gray-500 dark:text-gray-400">{{ title }}</p>
        <p class="mt-1 text-2xl font-bold text-gray-900 dark:text-white">{{ displayValue }}</p>
        <p v-if="subtitle" class="mt-1 text-xs text-gray-400 dark:text-gray-500">{{ subtitle }}</p>
      </div>
      <div :class="iconBgClass" class="p-3 rounded-xl">
        <slot name="icon">
          <div :class="iconClass" class="w-6 h-6" />
        </slot>
      </div>
    </div>
    <div v-if="change !== undefined" class="mt-3 flex items-center text-sm">
      <span :class="change >= 0 ? 'text-green-600' : 'text-red-600'" class="font-medium">
        {{ change >= 0 ? '+' : '' }}{{ change }}%
      </span>
      <span class="ml-1 text-gray-500 dark:text-gray-400">vs last month</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  value: [String, Number],
  subtitle: String,
  change: Number,
  color: { type: String, default: 'blue' },
  prefix: { type: String, default: '' }
})

const displayValue = computed(() => {
  if (props.prefix) return `${props.prefix}${props.value}`
  return props.value
})

const iconBgClass = computed(() => {
  const colors = {
    blue: 'bg-blue-50 dark:bg-blue-900/50',
    green: 'bg-green-50 dark:bg-green-900/50',
    purple: 'bg-purple-50 dark:bg-purple-900/50',
    orange: 'bg-orange-50 dark:bg-orange-900/50',
    red: 'bg-red-50 dark:bg-red-900/50',
    indigo: 'bg-indigo-50 dark:bg-indigo-900/50',
  }
  return colors[props.color] || colors.blue
})

const iconClass = computed(() => {
  const colors = {
    blue: 'text-blue-600 dark:text-blue-400',
    green: 'text-green-600 dark:text-green-400',
    purple: 'text-purple-600 dark:text-purple-400',
    orange: 'text-orange-600 dark:text-orange-400',
    red: 'text-red-600 dark:text-red-400',
    indigo: 'text-indigo-600 dark:text-indigo-400',
  }
  return colors[props.color] || colors.blue
})
</script>
