<template>
  <div>
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-medium text-gray-900 dark:text-white">Membership Plans</h3>
      <button @click="openCreateModal" class="btn-primary">
        <svg class="w-5 h-5 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Create Plan
      </button>
    </div>

    <LoadingSpinner v-if="planStore.loading" />

    <EmptyState v-else-if="!planStore.plans.length" title="No plans found" message="Create your first membership plan.">
      <template #action>
        <button @click="openCreateModal" class="btn-primary">Create Plan</button>
      </template>
    </EmptyState>

    <!-- Plans Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="plan in planStore.plans" :key="plan.id" class="card overflow-hidden">
        <!-- Plan Type Badge -->
        <div :class="planHeaderClass(plan.planType)" class="px-6 py-4">
          <div class="flex items-center justify-between">
            <span class="text-sm font-medium text-white/80">{{ plan.planType }}</span>
            <div class="flex gap-1">
              <button @click="editPlan(plan)" class="p-1.5 text-white/70 hover:text-white rounded-lg hover:bg-white/20">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
              </button>
              <button @click="confirmDelete(plan)" class="p-1.5 text-white/70 hover:text-white rounded-lg hover:bg-white/20">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
              </button>
            </div>
          </div>
          <h3 class="text-xl font-bold text-white mt-2">{{ plan.name }}</h3>
          <div class="mt-3">
            <span class="text-3xl font-bold text-white">₹{{ plan.price }}</span>
            <span class="text-white/70 ml-1">/ {{ plan.durationMonths }} month{{ plan.durationMonths > 1 ? 's' : '' }}</span>
          </div>
        </div>

        <div class="px-6 py-4">
          <div class="space-y-2">
            <div v-if="plan.benefits" class="text-sm text-gray-600 dark:text-gray-400">
              <p v-for="(benefit, i) in plan.benefits.split(',')" :key="i" class="flex items-center gap-2 py-1">
                <svg class="w-4 h-4 text-green-500 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
                </svg>
                {{ benefit.trim() }}
              </p>
            </div>
          </div>

          <div class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700 flex items-center justify-between">
            <span class="text-sm text-gray-500">Active Members</span>
            <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ plan.memberCount || 0 }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Plan Form Modal -->
    <ModalDialog v-model="showModal" :title="editingPlan ? 'Edit Plan' : 'Create New Plan'" size="md">
      <form @submit.prevent="savePlan" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Plan Name *</label>
          <input v-model="form.name" type="text" required class="input-field" placeholder="e.g., Premium Monthly" />
        </div>
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Plan Type *</label>
            <select v-model="form.planType" required class="input-field">
              <option value="">Select</option>
              <option value="MONTHLY">Monthly</option>
              <option value="QUARTERLY">Quarterly</option>
              <option value="YEARLY">Yearly</option>
            </select>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Duration (Months) *</label>
            <input v-model.number="form.durationMonths" type="number" min="1" required class="input-field" />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Price (₹) *</label>
          <input v-model.number="form.price" type="number" min="1" step="0.01" required class="input-field" />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Benefits</label>
          <textarea v-model="form.benefits" rows="3" class="input-field" placeholder="Comma separated: Gym access, Personal trainer, Sauna"></textarea>
        </div>
      </form>
      <template #footer>
        <button @click="showModal = false" class="btn-secondary">Cancel</button>
        <button @click="savePlan" :disabled="saving" class="btn-primary">
          {{ saving ? 'Saving...' : (editingPlan ? 'Update' : 'Create') }}
        </button>
      </template>
    </ModalDialog>

    <!-- Delete Confirmation -->
    <ModalDialog v-model="showDeleteModal" title="Delete Plan" size="sm">
      <p class="text-gray-600 dark:text-gray-300">
        Are you sure you want to delete <strong>{{ deletingPlan?.name }}</strong>?
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
import { usePlanStore } from '@/stores/plans'
import { useToast } from 'vue-toastification'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import EmptyState from '@/components/EmptyState.vue'
import ModalDialog from '@/components/ModalDialog.vue'

const planStore = usePlanStore()
const toast = useToast()

const showModal = ref(false)
const showDeleteModal = ref(false)
const editingPlan = ref(null)
const deletingPlan = ref(null)
const saving = ref(false)
const form = ref(getEmptyForm())

function getEmptyForm() {
  return { name: '', planType: '', durationMonths: 1, price: 0, benefits: '' }
}

onMounted(() => planStore.fetchPlans())

function openCreateModal() {
  editingPlan.value = null
  form.value = getEmptyForm()
  showModal.value = true
}

function editPlan(plan) {
  editingPlan.value = plan
  form.value = { ...plan }
  showModal.value = true
}

function confirmDelete(plan) {
  deletingPlan.value = plan
  showDeleteModal.value = true
}

async function savePlan() {
  saving.value = true
  try {
    if (editingPlan.value) {
      await planStore.updatePlan(editingPlan.value.id, form.value)
      toast.success('Plan updated successfully')
    } else {
      await planStore.createPlan(form.value)
      toast.success('Plan created successfully')
    }
    showModal.value = false
    planStore.fetchPlans()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Failed to save plan')
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  saving.value = true
  try {
    await planStore.deletePlan(deletingPlan.value.id)
    toast.success('Plan deleted successfully')
    showDeleteModal.value = false
  } catch (err) {
    toast.error('Failed to delete plan')
  } finally {
    saving.value = false
  }
}

function planHeaderClass(type) {
  const colors = {
    MONTHLY: 'bg-gradient-to-r from-blue-500 to-blue-600',
    QUARTERLY: 'bg-gradient-to-r from-purple-500 to-purple-600',
    YEARLY: 'bg-gradient-to-r from-emerald-500 to-emerald-600',
  }
  return colors[type] || colors.MONTHLY
}
</script>
