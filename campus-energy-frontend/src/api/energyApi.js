import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',  // 修改为/api前缀，通过代理转发
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    if (res.code !== 200) {
      const errorMessage = res.message || '请求失败';
      ElMessage.error(errorMessage)

      // token过期处理
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/login')
      }

      return Promise.reject(new Error(errorMessage))
    }

    return res.data
  },
  error => {
    console.error('API Error:', error)

    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data?.message || '请求失败')
      }
    } else if (error.request) {
      ElMessage.error('网络错误，请检查网络连接')
      return Promise.reject(new Error('网络错误，请检查网络连接'))
    } else {
      ElMessage.error('请求配置错误')
      return Promise.reject(new Error('请求配置错误'))
    }

    return Promise.reject(error)
  }
)

// API接口定义
export const api = {
  // 认证相关
  auth: {
    login(data) {
      return service.post('/auth/login', data)
    },
    register(data) {
      return service.post('/auth/register', data)
    },
    logout() {
      return service.post('/auth/logout')
    }
  },

  // 建筑管理
  building: {
    getAll() {
      return service.get('/buildings/all')
    },
    getList(params) {
      return service.get('/buildings', { params })
    },
    getById(id) {
      return service.get(`/buildings/${id}`)
    },
    create(data) {
      return service.post('/buildings', data)
    },
    update(id, data) {
      return service.put(`/buildings/${id}`, data)
    },
    delete(id) {
      return service.delete(`/buildings/${id}`)
    }
  },

  // 设备管理
  meter: {
    getAll(params) {
      return service.get('/meters', { params })
    },
    getById(id) {
      return service.get(`/meters/${id}`)
    },
    getBySerialNumber(sn) {
      return service.get(`/meters/sn/${sn}`)
    },
    create(data) {
      return service.post('/meters', data)
    },
    update(id, data) {
      return service.put(`/meters/${id}`, data)
    },
    delete(id) {
      return service.delete(`/meters/${id}`)
    },
    deactivate(id) {
      return service.patch(`/meters/${id}/deactivate`)
    },
    getByBuilding(buildingId) {
      return service.get(`/meters/building/${buildingId}`)
    }
  },

  // 能耗数据
  energyData: {
    getLatest(meterId) {
      return service.get(`/energy-data/meter/${meterId}/latest`)
    },
    getRecent(meterId, limit = 10) {
      return service.get(`/energy-data/meter/${meterId}/recent?limit=${limit}`)
    },
    getHistory(meterId, startDate, endDate) {
      return service.get(`/energy-data/meter/${meterId}/history`, {
        params: { startDate, endDate }
      })
    },
    getPowerTrend(meterId, limit = 10) {
      return service.get(`/energy-data/chart/power-trend`, {
        params: { meterId, limit }
      })
    },
    getEnergyDistribution() {
      return service.get('/energy-data/chart/energy-distribution')
    },
    getTodayCost() {
      return service.get('/energy-data/cost/today')
    }
  },

  // 告警管理
  alert: {
    getList(params) {
      return service.get('/alerts', { params })
    },
    getById(id) {
      return service.get(`/alerts/${id}`)
    },
    resolve(id) {
      return service.patch(`/alerts/${id}/resolve`)
    },
    getUnresolvedCount() {
      return service.get('/alerts/count/unresolved')
    }
  }
}