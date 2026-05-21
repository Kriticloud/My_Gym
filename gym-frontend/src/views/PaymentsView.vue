<template>
  <div>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
      <h3 class="text-lg font-medium text-gray-900 dark:text-white">Payment Records</h3>
      <button @click="openCreateModal" class="btn-primary">
        <svg class="w-5 h-5 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Record Payment
      </button>
    </div>

    <LoadingSpinner v-if="paymentStore.loading" />

    <EmptyState v-else-if="!paymentStore.payments.length" title="No payments recorded" message="Record your first payment.">
      <template #action>
        <button @click="openCreateModal" class="btn-primary">Record Payment</button>
      </template>
    </EmptyState>

    <!-- Payments Table -->
    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="table-header">Member</th>
              <th class="table-header">Plan</th>
              <th class="table-header">Amount</th>
              <th class="table-header">Method</th>
              <th class="table-header">Status</th>
              <th class="table-header">Date</th>
              <th class="table-header text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="payment in paymentStore.payments" :key="payment.id" class="hover:bg-gray-50 dark:hover:bg-gray-700/50">
              <td class="table-cell font-medium">{{ payment.memberName }}</td>
              <td class="table-cell">{{ payment.membershipPlanName || '—' }}</td>
              <td class="table-cell font-semibold">₹{{ payment.amount }}</td>
              <td class="table-cell">
                <span class="inline-flex items-center gap-1.5">
                  <span :class="methodIcon(payment.paymentMethod)" class="w-2 h-2 rounded-full"></span>
                  {{ payment.paymentMethod }}
                </span>
              </td>
              <td class="table-cell">
                <span :class="payment.paymentStatus === 'PAID' ? 'badge-paid' : 'badge-pending'">
                  {{ payment.paymentStatus }}
                </span>
              </td>
              <td class="table-cell">{{ formatDate(payment.paymentDate) }}</td>
              <td class="table-cell text-right">
                <button
                  v-if="payment.paymentStatus === 'PENDING'"
                  @click="markAsPaid(payment)"
                  class="text-xs btn-success py-1 px-2"
                >
                  Mark Paid
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Pagination -->
      <div v-if="paymentStore.pagination" class="px-6 py-3 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
        <p class="text-sm text-gray-500">
          Showing {{ paymentStore.payments.length }} of {{ paymentStore.pagination.totalElements }} payments
        </p>
        <div class="flex gap-2">
          <button
            @click="loadPage(paymentStore.pagination.currentPage - 1)"
            :disabled="paymentStore.pagination.currentPage === 0"
            class="btn-secondary text-sm py-1"
          >Previous</button>
          <button
            @click="loadPage(paymentStore.pagination.currentPage + 1)"
            :disabled="paymentStore.pagination.currentPage >= paymentStore.pagination.totalPages - 1"
            class="btn-secondary text-sm py-1"
          >Next</button>
        </div>
      </div>
    </div>

    <!-- Payment Form Modal -->
    <ModalDialog v-model="showModal" title="Record Payment" size="md">
      <form @submit.prevent="savePayment" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Member *</label>
          <select v-model="form.memberId" required class="input-field">
            <option :value="null">Select member</option>
            <option v-for="member in members" :key="member.id" :value="member.id">
              {{ member.firstName }} {{ member.lastName }}
            </option>
          </select>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Membership Plan</label>
          <select v-model="form.membershipPlanId" class="input-field" @change="onPlanSelect">
            <option :value="null">None (Custom payment)</option>
            <option v-for="plan in plans" :key="plan.id" :value="plan.id">
              {{ plan.name }} (₹{{ plan.price }})
            </option>
          </select>
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Amount (₹) *</label>
            <input v-model.number="form.amount" type="number" min="1" step="0.01" required class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Payment Method *</label>
            <select v-model="form.paymentMethod" required class="input-field">
              <option value="">Select</option>
              <option value="CASH">Cash</option>
              <option value="CARD">Card</option>
              <option value="UPI">UPI</option>
              <option value="BANK_TRANSFER">Bank Transfer</option>
            </select>
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Transaction ID</label>
          <input v-model="form.transactionId" type="text" class="input-field" placeholder="Optional" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Notes</label>
          <textarea v-model="form.notes" rows="2" class="input-field" placeholder="Optional notes"></textarea>
        </div>
      </form>
      <template #footer>
        <button @click="showModal = false" class="btn-secondary">Cancel</button>
        <button @click="savePayment" :disabled="saving" class="btn-primary">
          {{ saving ? 'Processing...' : 'Record Payment' }}
        </button>
      </template>
    </ModalDialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { usePaymentStore } from '@/stores/payments'
import { useMemberStore } from '@/stores/members'
import { usePlanStore } from '@/stores/plans'
import { useToast } from 'vue-toastification'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import EmptyState from '@/components/EmptyState.vue'
import ModalDialog from '@/components/ModalDialog.vue'

const paymentStore = usePaymentStore()
const memberStore = useMemberStore()
const planStore = usePlanStore()
const toast = useToast()

const showModal = ref(false)
const saving = ref(false)
const members = ref([])
const plans = ref([])
const form = ref(getEmptyForm())

function getEmptyForm() {
  return {
    memberId: null, membershipPlanId: null, amount: 0,
    paymentMethod: '', paymentStatus: 'PAID',
    transactionId: '', notes: ''
  }
}

onMounted(async () => {
  await paymentStore.fetchPayments()
  await memberStore.fetchMembers()
  members.value = memberStore.members
  await planStore.fetchPlans()
  plans.value = planStore.plans
})

function openCreateModal() {
  form.value = getEmptyForm()
  showModal.value = true
}

function onPlanSelect() {
  const plan = plans.value.find(p => p.id === form.value.membershipPlanId)
  if (plan) form.value.amount = plan.price
}

async function savePayment() {
  saving.value = true
  try {
    await paymentStore.createPayment(form.value)
    toast.success('Payment recorded successfully')
    showModal.value = false
    paymentStore.fetchPayments()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Failed to record payment')
  } finally {
    saving.value = false
  }
}

async function markAsPaid(payment) {
  try {
    await paymentStore.updatePaymentStatus(payment.id, 'PAID')
    toast.success('Payment marked as paid')
  } catch (err) {
    toast.error('Failed to update payment')
  }
}

function loadPage(page) {
  paymentStore.fetchPayments(page)
}

function formatDate(dateStr) {
  if (!dateStr) return '—'
  return new Date(dateStr).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })
}

function methodIcon(method) {
  const colors = { CASH: 'bg-green-500', CARD: 'bg-blue-500', UPI: 'bg-purple-500', BANK_TRANSFER: 'bg-orange-500' }
  return colors[method] || 'bg-gray-500'
}
</script>
