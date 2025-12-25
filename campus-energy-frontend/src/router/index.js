import { createRouter, createWebHistory } from 'vue-router'
import store from '@/store'
import { ElMessage } from 'element-plus'

// 页面组件
import Login from '@/views/login/Login.vue'
import Register from '@/views/register/Register.vue'
import Logout from '@/views/Logout.vue'
import Layout from '@/components/layout/Layout.vue'
import Dashboard from '@/views/dashboard/Dashboard.vue'
import BuildingList from '@/views/building/BuildingList.vue'
import MeterList from '@/views/meter/MeterList.vue'
import AlertList from '@/views/alert/AlertList.vue'
import Statistics from '@/views/statistics/Statistics.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/logout',
    name: 'Logout',
    component: Logout,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/login'
  },
  {
    path: '/app',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard,
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'buildings',
        name: 'Buildings',
        component: BuildingList,
        meta: { title: '建筑管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'meters',
        name: 'Meters',
        component: MeterList,
        meta: { title: '设备管理', icon: 'Monitor' }
      },
      {
        path: 'alerts',
        name: 'Alerts',
        component: AlertList,
        meta: { title: '告警管理', icon: 'Warning' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: Statistics,
        meta: { title: '统计分析', icon: 'DataAnalysis' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫 - 恢复认证检查
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = store.getters.userRole

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && token) {
    next('/')
  } else {
    // 权限控制
    if (to.meta.requiresAdmin && userRole !== 'ADMIN') {
      ElMessage.error('权限不足')
      next('/app/dashboard')
    } else {
      next()
    }
  }
});

export default router