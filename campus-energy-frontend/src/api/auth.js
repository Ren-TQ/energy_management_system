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
 * - 本文件属于Model层，负责用户认证相关的数据访问
 * - 所有方法返回Promise，由ViewModel层调用
 * - 不包含业务逻辑，只负责数据交互
 * 
 * @module AuthModel
 */

import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 用户注册
export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

// 获取所有用户
export function getUsers() {
  return request({
    url: '/auth/users',
    method: 'get'
  })
}

// 更新用户
export function updateUser(id, data) {
  return request({
    url: `/auth/users/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `/auth/users/${id}`,
    method: 'delete'
  })
}

