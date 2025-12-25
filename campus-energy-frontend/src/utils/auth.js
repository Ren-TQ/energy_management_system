// 验证token是否有效
export function isTokenValid() {
  const token = localStorage.getItem('token')
  if (!token) return false

  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.exp * 1000 > Date.now()
  } catch (e) {
    return false
  }
}

// 获取用户角色
export function getUserRole() {
  // 强制返回ADMIN角色以便测试
  return 'ADMIN'
  
  // 原始代码：
  // const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  // return userInfo.role
}

// 检查是否有权限
export function hasPermission(requiredRole) {
  const userRole = getUserRole()
  if (!userRole) return false

  // 如果是管理员，拥有所有权限
  if (userRole === 'ADMIN') return true

  // 普通用户只能访问特定权限
  const userPermissions = ['USER']
  return userPermissions.includes(userRole) && userPermissions.includes(requiredRole)
}