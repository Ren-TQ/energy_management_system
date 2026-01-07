/**
 * ============================================
 * 设计模式：Singleton Pattern（单例模式）
 * ============================================
 * 
 * 模式类型：创建型设计模式
 * 
 * 模式说明：
 * 确保一个类只有一个实例，并提供一个全局访问点。
 * 
 * 在此项目中的应用：
 * - Pinia Store本身就是单例模式
 * - 整个应用只有一个user store实例
 * - 通过useUserStore()获取同一个实例
 * 
 * 优势：
 * 1. 全局状态统一管理
 * 2. 避免状态不一致
 * 3. 节省内存资源
 * 
 * 代码位置：
 * - Store定义：src/stores/user.js
 * - 使用位置：所有需要用户信息的组件
 * ============================================
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi } from '@/api/auth'
import router from '@/router'

// ============================================
// 设计模式：Singleton Pattern（单例模式）
// Pinia Store自动实现单例，整个应用只有一个实例
// ============================================
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

