import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/MainLayout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览', icon: 'Monitor' }
      },
      {
        path: 'building',
        name: 'Building',
        component: () => import('@/views/Building.vue'),
        meta: { title: '建筑管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'device',
        name: 'Device',
        component: () => import('@/views/Device.vue'),
        meta: { title: '设备管理', icon: 'Cpu' }
      },
      {
        path: 'energy',
        name: 'Energy',
        component: () => import('@/views/Energy.vue'),
        meta: { title: '能耗数据', icon: 'TrendCharts' }
      },
      {
        path: 'alert',
        name: 'Alert',
        component: () => import('@/views/Alert.vue'),
        meta: { title: '告警管理', icon: 'Bell' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/User.vue'),
        meta: { title: '用户管理', icon: 'User', requiresAdmin: true }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 智慧校园能耗监测平台` : '智慧校园能耗监测平台'
  
  const token = localStorage.getItem('token')
  
  // 需要登录的页面
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
    return
  }
  
  // 已登录访问登录页，跳转首页
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }
  
  // 检查管理员权限
  if (to.meta.requiresAdmin) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.role !== 'ADMIN') {
      next('/dashboard')
      return
    }
  }
  
  next()
})

export default router

