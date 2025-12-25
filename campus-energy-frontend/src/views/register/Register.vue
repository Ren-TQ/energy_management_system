<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h1>智慧校园能耗监测系统</h1>
        <p>能源管理，智能监控，安全高效</p>
      </div>

      <div class="register-form">
        <h2>用户注册</h2>

        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="请输入邮箱"
              size="large"
              :prefix-icon="Message"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请确认密码"
              size="large"
              :prefix-icon="Lock"
            />
          </el-form-item>

          <el-form-item prop="role">
            <el-radio-group v-model="registerForm.role" class="radio-group">
              <el-radio label="USER" size="large">普通用户</el-radio>
              <el-radio label="ADMIN" size="large">管理员</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleRegister"
              class="register-btn"
            >
              注册
            </el-button>
          </el-form-item>
        </el-form>

        <div class="register-footer">
          <p>已有账号？<el-link type="primary" @click="goToLogin">立即登录</el-link></p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'

const router = useRouter()

const registerFormRef = ref()
const loading = ref(false)

const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'USER'
})

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== registerForm.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 处理注册
async function handleRegister() {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true

    const response = await api.auth.register({
      username: registerForm.username,
      email: registerForm.email,
      password: registerForm.password,
      role: registerForm.role
    })

    ElMessage.success('注册成功')
    router.push('/login')

  } catch (error) {
    console.error('注册失败:', error)
    ElMessage.error(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

// 跳转到登录页面
function goToLogin() {
  router.push('/login')
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-container .register-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.register-container .register-box .register-header {
  padding: 40px 30px 20px;
  text-align: center;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  color: white;
}

.register-container .register-box .register-header h1 {
  margin: 0 0 10px;
  font-size: 24px;
  font-weight: 600;
}

.register-container .register-box .register-header p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.register-container .register-box .register-form {
  padding: 30px;
}

.register-container .register-box .register-form h2 {
  margin: 0 0 30px;
  text-align: center;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

.register-container .register-box .register-form .form .register-btn {
  width: 100%;
  margin-top: 10px;
}

.register-container .register-box .register-form .radio-group {
  width: 100%;
  display: flex;
  flex-direction: column;
}

.register-container .register-box .register-form .radio-group .el-radio {
  margin-bottom: 10px;
}

.register-container .register-box .register-form .register-footer {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.register-container .register-box .register-form .register-footer .el-link {
  font-size: 14px;
}

@media (max-width: 480px) {
  .register-container {
    padding: 10px;
  }

  .register-container .register-box {
    max-width: 100%;
  }

  .register-container .register-box .register-header h1 {
    font-size: 20px;
  }
}
</style>