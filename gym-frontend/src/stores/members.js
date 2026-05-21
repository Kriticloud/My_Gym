import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api/axios'

export const useMemberStore = defineStore('members', () => {
  const members = ref([])
  const currentMember = ref(null)
  const loading = ref(false)
  const searchResults = ref(null)

  async function fetchMembers() {
    loading.value = true
    try {
      const response = await api.get('/members')
      members.value = response.data.data
    } finally {
      loading.value = false
    }
  }

  async function searchMembers(query, page = 0, size = 10) {
    loading.value = true
    try {
      const response = await api.get('/members/search', { params: { query, page, size } })
      searchResults.value = response.data.data
      return response.data.data
    } finally {
      loading.value = false
    }
  }

  async function fetchMember(id) {
    loading.value = true
    try {
      const response = await api.get(`/members/${id}`)
      currentMember.value = response.data.data
      return response.data.data
    } finally {
      loading.value = false
    }
  }

  async function createMember(memberData) {
    const response = await api.post('/members', memberData)
    members.value.push(response.data.data)
    return response.data.data
  }

  async function updateMember(id, memberData) {
    const response = await api.put(`/members/${id}`, memberData)
    const index = members.value.findIndex(m => m.id === id)
    if (index !== -1) members.value[index] = response.data.data
    return response.data.data
  }

  async function deleteMember(id) {
    await api.delete(`/members/${id}`)
    members.value = members.value.filter(m => m.id !== id)
  }

  async function activateMembership(memberId, planId) {
    const response = await api.post(`/members/${memberId}/activate/${planId}`)
    return response.data.data
  }

  return {
    members, currentMember, loading, searchResults,
    fetchMembers, searchMembers, fetchMember,
    createMember, updateMember, deleteMember, activateMembership
  }
})
