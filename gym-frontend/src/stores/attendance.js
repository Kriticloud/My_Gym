import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const useAttendanceStore = defineStore('attendance', () => {
  const records = ref([])
  const pagination = ref(null)
  const loading = ref(false)

  async function fetchAttendance(page = 0, size = 20) {
    loading.value = true
    try {
      const response = await api.get('/attendance', { params: { page, size } })
      const pageData = response.data.data
      records.value = pageData.content
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

  async function fetchByMember(memberId) {
    loading.value = true
    try {
      const response = await api.get(`/attendance/member/${memberId}`)
      return response.data.data
    } finally {
      loading.value = false
    }
  }

  async function checkIn(memberId, method = 'MANUAL') {
    const response = await api.post(`/attendance/checkin/${memberId}`, { method })
    records.value.unshift(response.data.data)
    return response.data.data
  }

  async function checkInByQr(qrCode) {
    const response = await api.post('/attendance/checkin/qr', { qrCode })
    records.value.unshift(response.data.data)
    return response.data.data
  }

  async function checkOut(memberId) {
    const response = await api.post(`/attendance/checkout/${memberId}`)
    const index = records.value.findIndex(r => r.memberId === memberId && r.status === 'CHECKED_IN')
    if (index !== -1) records.value[index] = response.data.data
    return response.data.data
  }

  return {
    records, pagination, loading,
    fetchAttendance, fetchByMember,
    checkIn, checkInByQr, checkOut
  }
})
