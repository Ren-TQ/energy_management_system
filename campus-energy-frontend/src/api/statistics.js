import request from '@/utils/request'

// 获取系统概览统计
export function getOverviewStatistics() {
  return request({
    url: '/statistics/overview',
    method: 'get'
  })
}

