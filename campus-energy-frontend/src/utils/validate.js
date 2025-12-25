// 验证邮箱
export function validateEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

// 验证手机号
export function validatePhone(phone) {
  const re = /^1[3-9]\d{9}$/
  return re.test(phone)
}

// 验证密码强度
export function validatePassword(password) {
  const hasLowerCase = /[a-z]/.test(password)
  const hasUpperCase = /[A-Z]/.test(password)
  const hasNumbers = /\d/.test(password)
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password)

  let strength = 0
  if (password.length >= 8) strength++
  if (hasLowerCase) strength++
  if (hasUpperCase) strength++
  if (hasNumbers) strength++
  if (hasSpecialChar) strength++

  return {
    strength,
    isValid: strength >= 3,
    tips: getPasswordTips(password)
  }
}

function getPasswordTips(password) {
  const tips = []
  if (password.length < 8) tips.push('密码长度至少8位')
  if (!/[a-z]/.test(password)) tips.push('包含小写字母')
  if (!/[A-Z]/.test(password)) tips.push('包含大写字母')
  if (!/\d/.test(password)) tips.push('包含数字')
  if (!/[!@#$%^&*(),.?":{}|<>]/.test(password)) tips.push('包含特殊字符')
  return tips
}

// 验证设备序列号
export function validateSerialNumber(sn) {
  const re = /^[A-Z0-9_]+$/
  return re.test(sn) && sn.length >= 3
}

// 验证功率值
export function validatePower(power) {
  return !isNaN(power) && power >= 0 && power <= 100000
}

// 验证电压值
export function validateVoltage(voltage) {
  return !isNaN(voltage) && voltage >= 0 && voltage <= 1000
}
