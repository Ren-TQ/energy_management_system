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
 * - 本文件属于Model层，负责统计数据相关的数据访问
 * - 所有方法返回Promise，由ViewModel层调用
 * - 不包含业务逻辑，只负责数据交互
 * 
 * @module StatisticsModel
 */

import request from '@/utils/request'

// 获取系统概览统计
export function getOverviewStatistics() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

