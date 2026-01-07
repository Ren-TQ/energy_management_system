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

