import request from '@/utils/request'

// 获取所有告警（分页）
export function getAlerts(params) {
  return request({
    url: '/alerts',
    method: 'get',
    params
  })
}

// 获取设备告警
export function getAlertsByDevice(deviceId, params) {
  return request({
    url: `/alerts/device/${deviceId}`,
    method: 'get',
    params
  })
}

// 获取未处理告警
export function getUnresolvedAlerts() {
  return request({
    url: '/alerts/unresolved',
    method: 'get'
  })
}

// 获取最近告警
export function getRecentAlerts() {
  return request({
    url: '/alerts/recent',
    method: 'get'
  })
}

// 获取今日告警数量
export function getTodayAlertCount() {
  return request({
    url: '/alerts/count/today',
    method: 'get'
  })
}

// 获取未处理告警数量
export function getUnresolvedAlertCount() {
  return request({
    url: '/alerts/count/unresolved',
    method: 'get'
  })
}

// 获取告警类型统计
export function getAlertTypeStats() {
  return request({
    url: '/alerts/stats/type',
    method: 'get'
  })
}

// 处理告警
export function resolveAlert(id, resolveNote) {
  return request({
    url: `/alerts/${id}/resolve`,
    method: 'post',
    params: { resolveNote }
  })
}

