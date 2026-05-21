<template>
  <div>
    <LoadingSpinner v-if="loading" />

    <div v-else-if="member">
      <!-- Back Button -->
      <button @click="$router.back()" class="flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 dark:hover:text-gray-300 mb-4">
        <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" /></svg>
        Back to Members
      </button>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <!-- Profile Card -->
        <div class="card p-6">
          <div class="text-center">
            <div class="w-20 h-20 mx-auto rounded-full bg-primary-100 dark:bg-primary-900 flex items-center justify-center mb-4">
              <span class="text-2xl font-bold text-primary-700 dark:text-primary-300">
                {{ member.firstName?.charAt(0) }}{{ member.lastName?.charAt(0) }}
              </span>
            </div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-white">{{ member.firstName }} {{ member.lastName }}</h2>
            <span :class="statusBadge(member.membershipStatus)" class="mt-2">{{ member.membershipStatus }}</span>
          </div>

          <div class="mt-6 space-y-3">
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">Phone</span>
              <span class="text-gray-900 dark:text-white">{{ member.phone }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">Email</span>
              <span class="text-gray-900 dark:text-white">{{ member.email || '—' }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">Gender</span>
              <span class="text-gray-900 dark:text-white">{{ member.gender }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">Age</span>
              <span class="text-gray-900 dark:text-white">{{ member.age || '—' }}</span>
            </div>
            <div class="flex justify-between text-sm">
              <span class="text-gray-500">Address</span>
              <span class="text-gray-900 dark:text-white text-right max-w-[60%]">{{ member.address || '—' }}</span>
            </div>
          </div>

          <!-- QR Code -->
          <div v-if="member.qrCode" class="mt-6 text-center">
            <p class="text-sm text-gray-500 mb-2">Member QR Code</p>
            <div class="inline-block p-2 bg-white rounded-lg">
              <div class="text-xs text-gray-400 mt-1">{{ member.qrCode }}</div>
            </div>
          </div>
        </div>

        <!-- Membership & Trainer -->
        <div class="space-y-6">
          <div class="card p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Membership Details</h3>
            <div class="space-y-3">
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">Plan</span>
                <span class="text-gray-900 dark:text-white font-medium">{{ member.membershipPlanName || 'No plan' }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">Start Date</span>
                <span class="text-gray-900 dark:text-white">{{ member.membershipStartDate || '—' }}</span>
              </div>
              <div class="flex justify-between text-sm">
                <span class="text-gray-500">End Date</span>
                <span class="text-gray-900 dark:text-white">{{ member.membershipEndDate || '—' }}</span>
              </div>
            </div>
          </div>

          <div class="card p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Assigned Trainer</h3>
            <div v-if="member.trainerName" class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-full bg-green-100 dark:bg-green-900 flex items-center justify-center">
                <svg class="w-5 h-5 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                </svg>
              </div>
              <span class="font-medium text-gray-900 dark:text-white">{{ member.trainerName }}</span>
            </div>
            <p v-else class="text-gray-500 text-sm">No trainer assigned</p>
          </div>
        </div>

        <!-- Payment History -->
        <div class="card p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Payment History</h3>
          <div v-if="payments.length" class="space-y-3">
            <div v-for="payment in payments" :key="payment.id" class="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
              <div>
                <p class="text-sm font-medium text-gray-900 dark:text-white">₹{{ payment.amount }}</p>
                <p class="text-xs text-gray-500">{{ payment.paymentMethod }} • {{ formatDate(payment.paymentDate) }}</p>
              </div>
              <span :class="payment.paymentStatus === 'PAID' ? 'badge-paid' : 'badge-pending'">
                {{ payment.paymentStatus }}
              </span>
            </div>
          </div>
          <p v-else class="text-gray-500 text-sm">No payments recorded</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useMemberStore } from '@/stores/members'
import { usePaymentStore } from '@/stores/payments'
import LoadingSpinner from '@/components/LoadingSpinner.vue'

const route = useRoute()
const memberStore = useMemberStore()
const paymentStore = usePaymentStore()

const member = ref(null)
const payments = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    member.value = await memberStore.fetchMember(route.params.id)
    payments.value = await paymentStore.fetchPaymentsByMember(route.params.id)
  } finally {
    loading.value = false
  }
})

function statusBadge(status) {
  const badges = { ACTIVE: 'badge-active', INACTIVE: 'badge-inactive', EXPIRED: 'badge-expired', FROZEN: 'badge-pending' }
  return badges[status] || 'badge-inactive'
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })
}
</script>
