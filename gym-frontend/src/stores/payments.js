import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const usePaymentStore = defineStore('payments', () => {
  const payments = ref([])
  const pagination = ref(null)
  const loading = ref(false)

  async function fetchPayments(page = 0, size = 20) {
    loading.value = true
    try {
      const response = await api.get('/payments', { params: { page, size } })
      const pageData = response.data.data
      payments.value = pageData.content
      pagination.value = {
        totalElements: pageData.totalElements,
        totalPages: pageData.totalPages,
        currentPage: pageData.number,
        size: pageData.size
      }
    } finally {
      loading.value = false
    }
  }

  async function fetchPaymentsByMember(memberId) {
    loading.value = true
    try {
      const response = await api.get(`/payments/member/${memberId}`)
      return response.data.data
    } finally {
      loading.value = false
    }
  }

  async function createPayment(paymentData) {
    const response = await api.post('/payments', paymentData)
    payments.value.unshift(response.data.data)
    return response.data.data
  }

  async function updatePaymentStatus(id, status) {
    const response = await api.patch(`/payments/${id}/status`, { status })
    const index = payments.value.findIndex(p => p.id === id)
    if (index !== -1) payments.value[index] = response.data.data
    return response.data.data
  }

  return {
    payments, pagination, loading,
    fetchPayments, fetchPaymentsByMember,
    createPayment, updatePaymentStatus
  }
})
