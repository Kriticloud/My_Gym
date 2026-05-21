<template>
  <div>
    <LoadingSpinner v-if="loading" message="Loading dashboard..." />

    <div v-else-if="data">
      <!-- Stats Grid -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard title="Total Members" :value="data.totalMembers" color="blue" subtitle="All registered members">
          <template #icon>
            <svg class="w-6 h-6 text-blue-600 dark:text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          </template>
        </StatCard>

        <StatCard title="Active Memberships" :value="data.activeMembers" color="green" subtitle="Currently active">
          <template #icon>
            <svg class="w-6 h-6 text-green-600 dark:text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </template>
        </StatCard>

        <StatCard title="Monthly Revenue" :value="formatCurrency(data.monthlyRevenue)" color="purple" subtitle="This month">
          <template #icon>
            <svg class="w-6 h-6 text-purple-600 dark:text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </template>
        </StatCard>

        <StatCard title="Today's Attendance" :value="data.todayAttendance" color="orange" subtitle="Members checked in">
          <template #icon>
            <svg class="w-6 h-6 text-orange-600 dark:text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
            </svg>
          </template>
        </StatCard>
      </div>

      <!-- Second Row Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <StatCard title="Total Revenue" :value="formatCurrency(data.totalRevenue)" color="indigo" subtitle="All time">
          <template #icon>
            <svg class="w-6 h-6 text-indigo-600 dark:text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
            </svg>
          </template>
        </StatCard>

        <StatCard title="Pending Payments" :value="data.pendingPayments" color="red" subtitle="Requires attention">
          <template #icon>
            <svg class="w-6 h-6 text-red-600 dark:text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </template>
        </StatCard>

        <StatCard title="Total Trainers" :value="data.totalTrainers" color="blue" subtitle="Active trainers">
          <template #icon>
            <svg class="w-6 h-6 text-blue-600 dark:text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5.121 17.804A13.937 13.937 0 0112 16c2.5 0 4.847.655 6.879 1.804M15 10a3 3 0 11-6 0 3 3 0 016 0zm6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </template>
        </StatCard>
      </div>

      <!-- Charts -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
        <!-- Revenue Chart -->
        <div class="card p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Revenue Trends</h3>
          <div class="h-64">
            <Line v-if="revenueChartData" :data="revenueChartData" :options="chartOptions" />
            <div v-else class="flex items-center justify-center h-full text-gray-400">No revenue data yet</div>
          </div>
        </div>

        <!-- Membership Growth -->
        <div class="card p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Membership Growth</h3>
          <div class="h-64">
            <Bar v-if="membershipChartData" :data="membershipChartData" :options="chartOptions" />
            <div v-else class="flex items-center justify-center h-full text-gray-400">No membership data yet</div>
          </div>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <!-- Attendance Chart -->
        <div class="card p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Daily Attendance (Last 30 Days)</h3>
          <div class="h-64">
            <Line v-if="attendanceChartData" :data="attendanceChartData" :options="chartOptions" />
            <div v-else class="flex items-center justify-center h-full text-gray-400">No attendance data yet</div>
          </div>
        </div>

        <!-- Membership Status Breakdown -->
        <div class="card p-6">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Membership Status</h3>
          <div class="h-64 flex items-center justify-center">
            <Doughnut v-if="statusChartData" :data="statusChartData" :options="doughnutOptions" />
            <div v-else class="text-gray-400">No status data yet</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useDashboardStore } from '@/stores/dashboard'
import { storeToRefs } from 'pinia'
import { Line, Bar, Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement,
  BarElement, ArcElement, Title, Tooltip, Legend, Filler
} from 'chart.js'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import StatCard from '@/components/StatCard.vue'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, ArcElement, Title, Tooltip, Legend, Filler)

const store = useDashboardStore()
const { data, loading } = storeToRefs(store)

onMounted(() => store.fetchDashboard())

function formatCurrency(value) {
  return new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', minimumFractionDigits: 0 }).format(value || 0)
}

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { display: false } },
  scales: {
    x: { grid: { display: false }, ticks: { font: { size: 11 } } },
    y: { beginAtZero: true, grid: { color: 'rgba(0,0,0,0.05)' }, ticks: { font: { size: 11 } } }
  }
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'bottom', labels: { padding: 20, font: { size: 12 } } }
  }
}

const revenueChartData = computed(() => {
  if (!data.value?.revenueChart?.length) return null
  return {
    labels: data.value.revenueChart.map(r => r.month),
    datasets: [{
      label: 'Revenue',
      data: data.value.revenueChart.map(r => r.amount),
      borderColor: '#8b5cf6',
      backgroundColor: 'rgba(139, 92, 246, 0.1)',
      fill: true,
      tension: 0.4
    }]
  }
})

const membershipChartData = computed(() => {
  if (!data.value?.membershipGrowth?.length) return null
  return {
    labels: data.value.membershipGrowth.map(r => r.month),
    datasets: [{
      label: 'New Members',
      data: data.value.membershipGrowth.map(r => r.count),
      backgroundColor: '#3b82f6',
      borderRadius: 8
    }]
  }
})

const attendanceChartData = computed(() => {
  if (!data.value?.attendanceChart?.length) return null
  return {
    labels: data.value.attendanceChart.map(r => r.date),
    datasets: [{
      label: 'Attendance',
      data: data.value.attendanceChart.map(r => r.count),
      borderColor: '#f97316',
      backgroundColor: 'rgba(249, 115, 22, 0.1)',
      fill: true,
      tension: 0.4
    }]
  }
})

const statusChartData = computed(() => {
  if (!data.value?.membershipStatusBreakdown || !Object.keys(data.value.membershipStatusBreakdown).length) return null
  const breakdown = data.value.membershipStatusBreakdown
  const colors = { ACTIVE: '#22c55e', INACTIVE: '#9ca3af', EXPIRED: '#ef4444', FROZEN: '#3b82f6' }
  return {
    labels: Object.keys(breakdown),
    datasets: [{
      data: Object.values(breakdown),
      backgroundColor: Object.keys(breakdown).map(k => colors[k] || '#6b7280')
    }]
  }
})
</script>
