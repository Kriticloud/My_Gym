<template>
  <div>
    <!-- Header -->
    <div class="flex items-center justify-between mb-6">
      <h3 class="text-lg font-medium text-gray-900 dark:text-white">Manage Trainers</h3>
      <button @click="openCreateModal" class="btn-primary">
        <svg class="w-5 h-5 mr-1.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
        </svg>
        Add Trainer
      </button>
    </div>

    <LoadingSpinner v-if="trainerStore.loading" />

    <EmptyState v-else-if="!trainerStore.trainers.length" title="No trainers found" message="Add your first trainer to get started.">
      <template #action>
        <button @click="openCreateModal" class="btn-primary">Add Trainer</button>
      </template>
    </EmptyState>

    <!-- Trainers Grid -->
    <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="trainer in trainerStore.trainers" :key="trainer.id" class="card p-6">
        <div class="flex items-start justify-between">
          <div class="flex items-center gap-3">
            <div class="w-12 h-12 rounded-full bg-green-100 dark:bg-green-900 flex items-center justify-center">
              <span class="text-lg font-semibold text-green-700 dark:text-green-300">
                {{ trainer.firstName?.charAt(0) }}{{ trainer.lastName?.charAt(0) }}
              </span>
            </div>
            <div>
              <h4 class="font-semibold text-gray-900 dark:text-white">{{ trainer.firstName }} {{ trainer.lastName }}</h4>
              <p class="text-sm text-gray-500">{{ trainer.specialization || 'General Trainer' }}</p>
            </div>
          </div>
          <div class="flex gap-1">
            <button @click="editTrainer(trainer)" class="p-1.5 text-gray-400 hover:text-primary-600 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" /></svg>
            </button>
            <button @click="confirmDelete(trainer)" class="p-1.5 text-gray-400 hover:text-red-600 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700">
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
            </button>
          </div>
        </div>

        <div class="mt-4 space-y-2">
          <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 5a2 2 0 012-2h3.28a1 1 0 01.948.684l1.498 4.493a1 1 0 01-.502 1.21l-2.257 1.13a11.042 11.042 0 005.516 5.516l1.13-2.257a1 1 0 011.21-.502l4.493 1.498a1 1 0 01.684.949V19a2 2 0 01-2 2h-1C9.716 21 3 14.284 3 6V5z" /></svg>
            {{ trainer.phone }}
          </div>
          <div v-if="trainer.email" class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>
            {{ trainer.email }}
          </div>
          <div class="flex items-center gap-2 text-sm text-gray-600 dark:text-gray-400">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
            {{ trainer.experienceYears || 0 }} years experience
          </div>
        </div>

        <div class="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
          <div class="flex items-center justify-between">
            <span class="text-sm text-gray-500">Assigned Members</span>
            <span class="text-sm font-semibold text-gray-900 dark:text-white">{{ trainer.memberCount || 0 }}</span>
          </div>
          <div class="mt-2 w-full bg-gray-200 dark:bg-gray-700 rounded-full h-2">
            <div
              class="bg-primary-600 h-2 rounded-full transition-all"
              :style="{ width: Math.min((trainer.memberCount || 0) * 10, 100) + '%' }"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- Trainer Form Modal -->
    <ModalDialog v-model="showModal" :title="editingTrainer ? 'Edit Trainer' : 'Add New Trainer'" size="md">
      <form @submit.prevent="saveTrainer" class="space-y-4">
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
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Specialization</label>
            <input v-model="form.specialization" type="text" class="input-field" placeholder="e.g., Weight Training, Yoga" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Experience (Years)</label>
            <input v-model.number="form.experienceYears" type="number" min="0" class="input-field" />
          </div>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Bio</label>
          <textarea v-model="form.bio" rows="3" class="input-field"></textarea>
        </div>
      </form>
      <template #footer>
        <button @click="showModal = false" class="btn-secondary">Cancel</button>
        <button @click="saveTrainer" :disabled="saving" class="btn-primary">
          {{ saving ? 'Saving...' : (editingTrainer ? 'Update' : 'Create') }}
        </button>
      </template>
    </ModalDialog>

    <!-- Delete Confirmation -->
    <ModalDialog v-model="showDeleteModal" title="Delete Trainer" size="sm">
      <p class="text-gray-600 dark:text-gray-300">
        Are you sure you want to delete <strong>{{ deletingTrainer?.firstName }} {{ deletingTrainer?.lastName }}</strong>?
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
import { useTrainerStore } from '@/stores/trainers'
import { useToast } from 'vue-toastification'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import EmptyState from '@/components/EmptyState.vue'
import ModalDialog from '@/components/ModalDialog.vue'

const trainerStore = useTrainerStore()
const toast = useToast()

const showModal = ref(false)
const showDeleteModal = ref(false)
const editingTrainer = ref(null)
const deletingTrainer = ref(null)
const saving = ref(false)
const form = ref(getEmptyForm())

function getEmptyForm() {
  return { firstName: '', lastName: '', email: '', phone: '', specialization: '', experienceYears: 0, bio: '' }
}

onMounted(() => trainerStore.fetchTrainers())

function openCreateModal() {
  editingTrainer.value = null
  form.value = getEmptyForm()
  showModal.value = true
}

function editTrainer(trainer) {
  editingTrainer.value = trainer
  form.value = { ...trainer }
  showModal.value = true
}

function confirmDelete(trainer) {
  deletingTrainer.value = trainer
  showDeleteModal.value = true
}

async function saveTrainer() {
  saving.value = true
  try {
    if (editingTrainer.value) {
      await trainerStore.updateTrainer(editingTrainer.value.id, form.value)
      toast.success('Trainer updated successfully')
    } else {
      await trainerStore.createTrainer(form.value)
      toast.success('Trainer created successfully')
    }
    showModal.value = false
    trainerStore.fetchTrainers()
  } catch (err) {
    toast.error(err.response?.data?.message || 'Failed to save trainer')
  } finally {
    saving.value = false
  }
}

async function handleDelete() {
  saving.value = true
  try {
    await trainerStore.deleteTrainer(deletingTrainer.value.id)
    toast.success('Trainer deleted successfully')
    showDeleteModal.value = false
  } catch (err) {
    toast.error('Failed to delete trainer')
  } finally {
    saving.value = false
  }
}
</script>
