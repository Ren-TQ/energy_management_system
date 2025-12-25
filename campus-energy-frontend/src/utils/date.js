import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

// 格式化日期
export function formatDate(date, format = 'YYYY-MM-DD') {
  return dayjs(date).format(format)
}

// 格式化时间
export function formatTime(date, format = 'HH:mm:ss') {
  return dayjs(date).format(format)
}

// 格式化日期时间
export function formatDateTime(date, format = 'YYYY-MM-DD HH:mm:ss') {
  return dayjs(date).format(format)
}

// 相对时间
export function fromNow(date) {
  return dayjs(date).fromNow()
}

// 获取今天日期
export function getToday() {
  return dayjs().format('YYYY-MM-DD')
}

// 获取几天前的日期
export function getDaysAgo(days) {
  return dayjs().subtract(days, 'day').format('YYYY-MM-DD')
}

// 获取日期范围
export function getDateRange(days = 7) {
  return {
    startDate: getDaysAgo(days),
    endDate: getToday()
  }
}
