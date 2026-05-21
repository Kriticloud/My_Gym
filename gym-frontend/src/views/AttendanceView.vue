<template>
  <div>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
      <h3 class="text-lg font-medium text-gray-900 dark:text-white">Attendance Tracking</h3>
      <div class="flex gap-2">
        <button @click="showQrModal = true" class="btn-secondary">
          <svg class="w-5 h-5 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v1m6 11h2m-6 0h-2v4m0-11v3m0 0h.01M12 12h4.01M16 20h4M4 12h4m12 0h.01M5 8h2a1 1 0 001-1V5a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1zm12 0h2a1 1 0 001-1V5a1 1 0 00-1-1h-2a1 1 0 00-1 1v2a1 1 0 001 1zM5 20h2a1 1 0 001-1v-2a1 1 0 00-1-1H5a1 1 0 00-1 1v2a1 1 0 001 1z" />
          </svg>
          QR Check-in
        </button>
        <button @click="showCheckInModal = true" class="btn-primary">
          <svg class="w-5 h-5 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
          </svg>
          Manual Check-in
        </button>
      </div>
    </div>

    <LoadingSpinner v-if="attendanceStore.loading" />

    <EmptyState v-else-if="!attendanceStore.records.length" title="No attendance records" message="Start tracking attendance by checking in members." />

    <!-- Attendance Table -->
    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="table-header">Member</th>
              <th class="table-header">Check-in Time</th>
              <th class="table-header">Check-out Time</th>
              <th class="table-header">Method</th>
              <th class="table-header">Status</th>
              <th class="table-header text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="record in attendanceStore.records" :key="record.id" class="hover:bg-gray-50 dark:hover:bg-gray-700/50">
              <td class="table-cell font-medium">{{ record.memberName }}</td>
              <td class="table-cell">{{ formatDateTime(record.checkInTime) }}</td>
              <td class="table-cell">{{ record.checkOutTime ? formatDateTime(record.checkOutTime) : '—' }}</td>
              <td class="table-cell">
                <span class="inline-flex items-center gap-1.5 text-xs">
                  <span :class="record.checkInMethod === 'QR_CODE' ? 'text-purple-600' : 'text-blue-600'">
                    {{ record.checkInMethod.replace('_', ' ') }}
                  </span>
                </span>
              </td>
              <td class="table-cell">
                <span :class="record.status === 'CHECKED_IN' ? 'badge-active' : 'badge-inactive'">
                  {{ record.status === 'CHECKED_IN' ? 'In Gym' : 'Left' }}
                </span>
              </td>
              <td class="table-cell text-right">
                <button
                  v-if="record.status === 'CHECKED_IN'"
                  @click="handleCheckOut(record.memberId)"
                  class="text-xs btn-secondary py-1 px-2"
                >
                  Check Out
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="attendanceStore.pagination" class="px-6 py-3 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
        <p class="text-sm text-gray-500">
          Showing {{ attendanceStore.records.length }} of {{ attendanceStore.pagination.totalElements }} records
        </p>
        <div class="flex gap-2">
          <button
            @click="loadPage(attendanceStore.pagination.currentPage - 1)"
            :disabled="attendanceStore.pagination.currentPage === 0"
            class="btn-secondary text-sm py-1"
          >Previous</button>
          <button
            @click="loadPage(attendanceStore.pagination.currentPage + 1)"
            :disabled="attendanceStore.pagination.currentPage >= attendanceStore.pagination.totalPages - 1"
            class="btn-secondary text-sm py-1"
          >Next</button>
        </div>
      </div>
    </div>

    <!-- Manual Check-in Modal -->
    <ModalDialog v-model="showCheckInModal" title="Manual Check-in" size="sm">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Select Member</label>
          <select v-model="selectedMemberId" class="input-field">
            <option :value="null">Choose member</option>
            <option v-for="member in members" :key="member.id" :value="member.id">
              {{ member.firstName }} {{ member.lastName }}
              <template v-if="member.membershipStatus !== 'ACTIVE'"> ({{ member.membershipStatus }})</template>
            </option>
          </select>
        </div>
      </div>
      <template #footer>
        <button @click="showCheckInModal = false" class="btn-secondary">Cancel</button>
        <button @click="handleCheckIn" :disabled="!selectedMemberId || processing" class="btn-primary">
          {{ processing ? 'Processing...' : 'Check In' }}
        </button>
      </template>
    </ModalDialog>

    <!-- QR Check-in Modal -->
    <ModalDialog v-model="showQrModal" title="QR Code Check-in" size="sm">
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Scan or Enter QR Code</label>
          <input v-model="qrCode" type="text" class="input-field" placeholder="Enter QR code value" />
        </div>
      </div>
      <template #footer>
        <button @click="showQrModal = false" class="btn-secondary">Cancel</button>
        <button @click="handleQrCheckIn" :disabled="!qrCode || processing" class="btn-primary">
          {{ processing ? 'Processing...' : 'Check In' }}
        </button>
      </template>
    </ModalDialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAttendanceStore } from '@/stores/attendance'
import { useMemberStore } from '@/stores/members'
import { useToast } from 'vue-toastification'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import EmptyState from '@/components/EmptyState.vue'
import ModalDialog from '@/components/ModalDialog.vue'

const attendanceStore = useAttendanceStore()
const memberStore = useMemberStore()
const toast = useToast()

const showCheckInModal = ref(false)
const showQrModal = ref(false)
const selectedMemberId = ref(null)
const qrCode = ref('')
const processing = ref(false)
const members = ref([])

onMounted(async () => {
  await attendanceStore.fetchAttendance()
  await memberStore.fetchMembers()
  members.value = memberStore.members
})

async function handleCheckIn() {
  processing.value = true
  try {
    await attendanceStore.checkIn(selectedMemberId.value, 'MANUAL')
    toast.success('Member checked in successfully')
    showCheckInModal.value = false
    selectedMemberId.value = null
    attendanceStore.fetchAttendance()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Check-in failed')
  } finally {
    processing.value = false
  }
}

async function handleQrCheckIn() {
  processing.value = true
  try {
    await attendanceStore.checkInByQr(qrCode.value)
    toast.success('QR check-in successful')
    showQrModal.value = false
    qrCode.value = ''
    attendanceStore.fetchAttendance()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Invalid QR code or check-in failed')
  } finally {
    processing.value = false
  }
}

async function handleCheckOut(memberId) {
  try {
    await attendanceStore.checkOut(memberId)
    toast.success('Member checked out')
    attendanceStore.fetchAttendance()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Check-out failed')
  }
}

function loadPage(page) {
  attendanceStore.fetchAttendance(page)
}

function formatDateTime(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleString('en-IN', {
    day: 'numeric', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>
