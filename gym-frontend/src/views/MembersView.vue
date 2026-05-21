<template>
  <div>
    <!-- Header -->
    <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
      <div class="relative flex-1 max-w-md">
        <svg class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
        </svg>
        <input
          v-model="searchQuery"
          @input="handleSearch"
          type="text"
          placeholder="Search members..."
          class="input-field pl-10"
        />
      </div>
      <button @click="openCreateModal" class="btn-primary">
        <svg class="w-5 h-5 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Add Member
      </button>
    </div>

    <!-- Loading -->
    <LoadingSpinner v-if="memberStore.loading" />

    <!-- Empty State -->
    <EmptyState v-else-if="!memberStore.members.length" title="No members found" message="Add your first gym member to get started.">
      <template #action>
        <button @click="openCreateModal" class="btn-primary">Add Member</button>
      </template>
    </EmptyState>

    <!-- Members Table -->
    <div v-else class="card overflow-hidden">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
          <thead class="bg-gray-50 dark:bg-gray-900/50">
            <tr>
              <th class="table-header">Member</th>
              <th class="table-header">Contact</th>
              <th class="table-header">Plan</th>
              <th class="table-header">Trainer</th>
              <th class="table-header">Status</th>
              <th class="table-header">Expiry</th>
              <th class="table-header text-right">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="member in memberStore.members" :key="member.id" class="hover:bg-gray-50 dark:hover:bg-gray-700/50 transition-colors">
              <td class="table-cell">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-full bg-primary-100 dark:bg-primary-900 flex items-center justify-center">
                    <span class="text-sm font-semibold text-primary-700 dark:text-primary-300">
                      {{ member.firstName?.charAt(0) }}{{ member.lastName?.charAt(0) }}
                    </span>
                  </div>
                  <div>
                    <router-link :to="`/members/${member.id}`" class="font-medium text-gray-900 dark:text-white hover:text-primary-600">
                      {{ member.firstName }} {{ member.lastName }}
                    </router-link>
                    <p class="text-xs text-gray-500">{{ member.gender }} {{ member.age ? `• ${member.age} yrs` : '' }}</p>
                  </div>
                </div>
              </td>
              <td class="table-cell">
                <p>{{ member.phone }}</p>
                <p class="text-xs text-gray-500">{{ member.email }}</p>
              </td>
              <td class="table-cell">{{ member.membershipPlanName || '—' }}</td>
              <td class="table-cell">{{ member.trainerName || '—' }}</td>
              <td class="table-cell">
                <span :class="statusBadge(member.membershipStatus)">{{ member.membershipStatus }}</span>
              </td>
              <td class="table-cell">{{ member.membershipEndDate || '—' }}</td>
              <td class="table-cell text-right">
                <div class="flex items-center justify-end gap-1">
                  <button @click="editMember(member)" class="p-1.5 text-gray-400 hover:text-primary-600 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700" title="Edit">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
                  </button>
                  <button @click="confirmDelete(member)" class="p-1.5 text-gray-400 hover:text-red-600 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700" title="Delete">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Member Form Modal -->
    <ModalDialog v-model="showModal" :title="editingMember ? 'Edit Member' : 'Add New Member'" size="lg">
      <form @submit.prevent="saveMember" class="space-y-4">
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">First Name *</label>
            <input v-model="form.firstName" type="text" required class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Last Name *</label>
            <input v-model="form.lastName" type="text" required class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Email</label>
            <input v-model="form.email" type="email" class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Phone *</label>
            <input v-model="form.phone" type="tel" required class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Gender *</label>
            <select v-model="form.gender" required class="input-field">
              <option value="">Select</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Date of Birth</label>
            <input v-model="form.dateOfBirth" type="date" class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Membership Plan</label>
            <select v-model="form.membershipPlanId" class="input-field">
              <option :value="null">None</option>
              <option v-for="plan in planStore.plans" :key="plan.id" :value="plan.id">
                {{ plan.name }} (₹{{ plan.price }})
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Trainer</label>
            <select v-model="form.trainerId" class="input-field">
              <option :value="null">None</option>
              <option v-for="trainer in trainerStore.trainers" :key="trainer.id" :value="trainer.id">
                {{ trainer.fullName || `${trainer.firstName} ${trainer.lastName}` }}
              </option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Emergency Contact</label>
            <input v-model="form.emergencyContact" type="tel" class="input-field" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Address</label>
            <input v-model="form.address" type="text" class="input-field" />
          </div>
        </div>
      </form>
      <template #footer>
        <button @click="showModal = false" class="btn-secondary">Cancel</button>
        <button @click="saveMember" :disabled="saving" class="btn-primary">
          {{ saving ? 'Saving...' : (editingMember ? 'Update' : 'Create') }}
        </button>
      </template>
    </ModalDialog>

    <!-- Delete Confirmation -->
    <ModalDialog v-model="showDeleteModal" title="Delete Member" size="sm">
      <p class="text-gray-600 dark:text-gray-300">
        Are you sure you want to delete <strong>{{ deletingMember?.firstName }} {{ deletingMember?.lastName }}</strong>? This action cannot be undone.
      </p>
      <template #footer>
        <button @click="showDeleteModal = false" class="btn-secondary">Cancel</button>
        <button @click="handleDelete" :disabled="saving" class="btn-danger">Delete</button>
      </template>
    </ModalDialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useMemberStore } from '@/stores/members'
import { usePlanStore } from '@/stores/plans'
import { useTrainerStore } from '@/stores/trainers'
import { useToast } from 'vue-toastification'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import EmptyState from '@/components/EmptyState.vue'
import ModalDialog from '@/components/ModalDialog.vue'

const memberStore = useMemberStore()
const planStore = usePlanStore()
const trainerStore = useTrainerStore()
const toast = useToast()

const searchQuery = ref('')
const showModal = ref(false)
const showDeleteModal = ref(false)
const editingMember = ref(null)
const deletingMember = ref(null)
const saving = ref(false)

const form = ref(getEmptyForm())

function getEmptyForm() {
  return {
    firstName: '', lastName: '', email: '', phone: '',
    gender: '', dateOfBirth: '', address: '',
    emergencyContact: '', membershipPlanId: null, trainerId: null
  }
}

onMounted(async () => {
  await Promise.all([
    memberStore.fetchMembers(),
    planStore.fetchPlans(),
    trainerStore.fetchTrainers()
  ])
})

function handleSearch() {
  if (searchQuery.value.length > 2) {
    memberStore.searchMembers(searchQuery.value)
  } else if (searchQuery.value.length === 0) {
    memberStore.fetchMembers()
  }
}

function openCreateModal() {
  editingMember.value = null
  form.value = getEmptyForm()
  showModal.value = true
}

function editMember(member) {
  editingMember.value = member
  form.value = { ...member }
  showModal.value = true
}

function confirmDelete(member) {
  deletingMember.value = member
  showDeleteModal.value = true
}

async function saveMember() {
  saving.value = true
  try {
    if (editingMember.value) {
      await memberStore.updateMember(editingMember.value.id, form.value)
      toast.success('Member updated successfully')
    } else {
      await memberStore.createMember(form.value)
      toast.success('Member created successfully')
    }
    showModal.value = false
    memberStore.fetchMembers()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Failed to save member')
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  saving.value = true
  try {
    await memberStore.deleteMember(deletingMember.value.id)
    toast.success('Member deleted successfully')
    showDeleteModal.value = false
  } catch (err) {
    toast.error('Failed to delete member')
  } finally {
    saving.value = false
  }
}

function statusBadge(status) {
  const badges = {
    ACTIVE: 'badge-active',
    INACTIVE: 'badge-inactive',
    EXPIRED: 'badge-expired',
    FROZEN: 'badge-pending'
  }
  return badges[status] || 'badge-inactive'
}
</script>
