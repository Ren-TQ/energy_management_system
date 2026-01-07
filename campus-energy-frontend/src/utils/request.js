/**
 * ============================================
 * 设计模式：Adapter Pattern（适配器模式）
 * ============================================
 * 
 * 模式类型：结构型设计模式
 * 
 * 模式说明：
 * 适配器模式将一个类的接口转换成客户希望的另一个接口。
 * 适配器模式使得原本由于接口不兼容而不能一起工作的类可以一起工作。
 * 
 * 在此项目中的应用：
 * - Target（目标接口）：统一的request函数接口
 * - Adapter（适配器）：axios实例 + 拦截器
 * - Adaptee（被适配者）：原生axios库
 * 
 * 实现细节：
 * 1. 请求拦截器：统一添加Token认证头
 * 2. 响应拦截器：统一处理响应格式和错误
 * 3. 将axios的接口适配为项目统一的接口格式
 * 
 * 优势：
 * 1. 统一API调用接口
 * 2. 集中处理认证、错误处理等横切关注点
 * 3. 易于维护和扩展
 * 
 * 代码位置：
 * - 适配器：src/utils/request.js
 * - 使用位置：所有api/*.js文件
 * ============================================
 */

import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// ============================================
// 设计模式：Adapter Pattern（适配器模式）
// 创建axios实例作为适配器
// ============================================
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// ============================================
// 设计模式：Adapter Pattern（适配器模式）
// 请求拦截器：适配请求，统一添加认证信息
// ============================================
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// ============================================
// 设计模式：Adapter Pattern（适配器模式）
// 响应拦截器：适配响应，统一处理响应格式和错误
// ============================================
service.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果响应码不是200，显示错误
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: 未授权，跳转登录
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      const status = error.response.status
      
      if (status === 401) {
        ElMessage.error('登录已过期，请重新登录')
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      } else if (status === 403) {
        ElMessage.error('没有权限访问')
      } else if (status === 404) {
        ElMessage.error('请求的资源不存在')
      } else if (status === 500) {
        ElMessage.error('服务器内部错误')
      } else {
        ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败，请检查后端服务是否启动')
    }
    
    return Promise.reject(error)
  }
)

export default service

