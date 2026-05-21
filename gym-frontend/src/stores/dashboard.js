import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const useDashboardStore = defineStore('dashboard', () => {
  const data = ref(null)
  const loading = ref(false)

  async function fetchDashboard() {
    loading.value = true
    try {
      const response = await api.get('/dashboard')
      data.value = response.data.data
    } finally {
      loading.value = false
    }
  }

  return { data, loading, fetchDashboard }
})
