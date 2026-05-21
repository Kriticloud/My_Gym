import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const usePlanStore = defineStore('plans', () => {
  const plans = ref([])
  const loading = ref(false)

  async function fetchPlans() {
    loading.value = true
    try {
      const response = await api.get('/plans')
      plans.value = response.data.data
    } finally {
      loading.value = false
    }
  }

  async function fetchActivePlans() {
    loading.value = true
    try {
      const response = await api.get('/plans/active')
      plans.value = response.data.data
    } finally {
      loading.value = false
    }
  }

  async function createPlan(planData) {
    const response = await api.post('/plans', planData)
    plans.value.push(response.data.data)
    return response.data.data
  }

  async function updatePlan(id, planData) {
    const response = await api.put(`/plans/${id}`, planData)
    const index = plans.value.findIndex(p => p.id === id)
    if (index !== -1) plans.value[index] = response.data.data
    return response.data.data
  }

  async function deletePlan(id) {
    await api.delete(`/plans/${id}`)
    plans.value = plans.value.filter(p => p.id !== id)
  }

  return {
    plans, loading,
    fetchPlans, fetchActivePlans,
    createPlan, updatePlan, deletePlan
  }
})
