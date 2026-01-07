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
 * - 本文件属于Model层，负责建筑相关的数据访问
 * - 所有方法返回Promise，由ViewModel层调用
 * - 不包含业务逻辑，只负责数据交互
 * 
 * @module BuildingModel
 */

import request from '@/utils/request'

// 获取所有建筑
export function getBuildings() {
  return request({
    url: '/buildings',
    method: 'get'
  })
}

// 获取建筑详情
export function getBuildingById(id) {
  return request({
    url: `/buildings/${id}`,
    method: 'get'
  })
}

// 获取所有分类
export function getCategories() {
  return request({
    url: '/buildings/categories',
    method: 'get'
  })
}

// 创建建筑
export function createBuilding(data) {
  return request({
    url: '/buildings',
    method: 'post',
    data
  })
}

// 更新建筑
export function updateBuilding(id, data) {
  return request({
    url: `/buildings/${id}`,
    method: 'put',
    data
  })
}

// 删除建筑
export function deleteBuilding(id) {
  return request({
    url: `/buildings/${id}`,
    method: 'delete'
  })
}

