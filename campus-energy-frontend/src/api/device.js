/**
 * ============================================
 * MVVM架构 - Model层 (数据模型层)
 * ============================================
 * 
 * 职责：
 * 1. 与后端API进行通信
 * 2. 封装数据请求方法
 * 3. 处理请求参数和响应数据
 * 
 * 说明：
 * - 本文件属于Model层，负责设备相关的数据访问
 * - 所有方法返回Promise，由ViewModel层调用
 * - 不包含业务逻辑，只负责数据交互
 * 
 * @module DeviceModel
 */

import request from '@/utils/request'

// 获取所有设备
export function getDevices() {
  return request({
    url: '/devices',
    method: 'get'
  })
}

// 获取设备详情
export function getDeviceById(id) {
  return request({
    url: `/devices/${id}`,
    method: 'get'
  })
}

// 根据建筑获取设备
export function getDevicesByBuilding(buildingId) {
  return request({
    url: `/devices/building/${buildingId}`,
    method: 'get'
  })
}

// 获取在线设备
export function getOnlineDevices() {
  return request({
    url: '/devices/online',
    method: 'get'
  })
}

// 创建设备
export function createDevice(data) {
  return request({
    url: '/devices',
    method: 'post',
    data
  })
}

// 更新设备
export function updateDevice(id, data) {
  return request({
    url: `/devices/${id}`,
    method: 'put',
    data
  })
}

// 更新设备状态
export function updateDeviceStatus(id, status) {
  return request({
    url: `/devices/${id}/status`,
    method: 'patch',
    params: { status }
  })
}

// 删除设备
export function deleteDevice(id) {
  return request({
    url: `/devices/${id}`,
    method: 'delete'
  })
}

