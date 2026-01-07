import request from '@/utils/request'

// 获取设备能耗数据（分页）
export function getEnergyDataByDevice(deviceId, params) {
  return request({
    url: `/energy-data/device/${deviceId}`,
    method: 'get',
    params
  })
}

// 获取设备最新能耗数据
export function getLatestEnergyData(deviceId) {
  return request({
    url: `/energy-data/device/${deviceId}/latest`,
    method: 'get'
  })
}

// 获取所有设备最新能耗数据
export function getLatestAllEnergyData() {
  return request({
    url: '/energy-data/latest-all',
    method: 'get'
  })
}

// 获取设备今日能耗数据
export function getTodayEnergyData(deviceId) {
  return request({
    url: `/energy-data/device/${deviceId}/today`,
    method: 'get'
  })
}

// 获取时间范围内的能耗数据
export function getEnergyDataByRange(deviceId, startTime, endTime) {
  return request({
    url: `/energy-data/device/${deviceId}/range`,
    method: 'get',
    params: { startTime, endTime }
  })
}

// 计算用电量
export function calculateConsumption(deviceId, startTime, endTime) {
  return request({
    url: `/energy-data/device/${deviceId}/consumption`,
    method: 'get',
    params: { startTime, endTime }
  })
}

