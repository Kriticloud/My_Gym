import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue')
      },
      {
        path: 'members',
        name: 'Members',
        component: () => import('@/views/MembersView.vue')
      },
      {
        path: 'members/:id',
        name: 'MemberProfile',
        component: () => import('@/views/MemberProfileView.vue')
      },
      {
        path: 'trainers',
        name: 'Trainers',
        component: () => import('@/views/TrainersView.vue')
      },
      {
        path: 'plans',
        name: 'Plans',
        component: () => import('@/views/PlansView.vue')
      },
      {
        path: 'payments',
        name: 'Payments',
        component: () => import('@/views/PaymentsView.vue')
      },
      {
        path: 'attendance',
        name: 'Attendance',
        component: () => import('@/views/AttendanceView.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth !== false && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.path === '/login' && authStore.isAuthenticated) {
    next('/')
  } else {
    next()
  }
})

export default router
