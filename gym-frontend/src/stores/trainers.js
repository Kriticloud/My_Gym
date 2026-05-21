import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const useTrainerStore = defineStore('trainers', () => {
  const trainers = ref([])
  const loading = ref(false)

  async function fetchTrainers() {
    loading.value = true
    try {
      const response = await api.get('/trainers')
      trainers.value = response.data.data
    } finally {
      loading.value = false
    }
  }

  async function fetchTrainersWithWorkload() {
    loading.value = true
    try {
      const response = await api.get('/trainers/workload')
      trainers.value = response.data.data
    } finally {
      loading.value = false
    }
  }

  async function createTrainer(trainerData) {
    const response = await api.post('/trainers', trainerData)
    trainers.value.push(response.data.data)
    return response.data.data
  }

  async function updateTrainer(id, trainerData) {
    const response = await api.put(`/trainers/${id}`, trainerData)
    const index = trainers.value.findIndex(t => t.id === id)
    if (index !== -1) trainers.value[index] = response.data.data
    return response.data.data
  }

  async function deleteTrainer(id) {
    await api.delete(`/trainers/${id}`)
    trainers.value = trainers.value.filter(t => t.id !== id)
  }

  return {
    trainers, loading,
    fetchTrainers, fetchTrainersWithWorkload,
    createTrainer, updateTrainer, deleteTrainer
  }
})
