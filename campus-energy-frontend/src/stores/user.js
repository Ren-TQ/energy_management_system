import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  
  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'ADMIN')
  const username = computed(() => userInfo.value.username || '')
  
  // 登录
  async function login(credentials) {
    const res = await loginApi(credentials)
    
    token.value = res.data.token
    userInfo.value = {
      userId: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      role: res.data.role,
      roleLabel: res.data.roleLabel
    }
    
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    
    return res
  }
  
  // 登出
  function logout() {
    token.value = ''
    userInfo.value = {}
    
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    router.push('/login')
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    username,
    login,
    logout
  }
})

