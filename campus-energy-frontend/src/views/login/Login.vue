<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>智慧校园能耗监测系统</h1>
        <p>能源管理，智能监控，安全高效</p>
      </div>

      <div class="login-form">
        <h2>用户登录</h2>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="login-btn"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-actions">
          <p>还没有账号？<el-link type="primary" @click="goToRegister">立即注册</el-link></p>
        </div>

        <div class="login-tips">
          <p>测试账号：</p>
          <p>管理员：admin / admin123</p>
          <p>普通用户：user / user123</p>
        </div>
      </div>

      <div class="login-footer">
        <p>© 2025 智慧校园能耗监测系统</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'

const router = useRouter()
const store = useStore()

const loginFormRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 处理登录
async function handleLogin() {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
    loading.value = true

    const response = await api.auth.login({
      username: loginForm.username,
      password: loginForm.password
    })

    // 保存用户信息
    store.dispatch('login', {
      token: response.token,
      userInfo: {
        username: response.username,
        role: response.role,
        userId: response.userId
      }
    })

    ElMessage.success('登录成功')
    router.push('/dashboard')

  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error(error.message || '登录失败，请检查用户名和密码')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页面
function goToRegister() {
  router.push('/register')
}

// 自动填充测试账号（仅开发环境）
if (import.meta.env.DEV) {
  loginForm.username = 'admin'
  loginForm.password = 'admin123'
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container .login-box {
  width: 100%;
  max-width: 420px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.login-container .login-box .login-header {
  padding: 40px 30px 20px;
  text-align: center;
  background: linear-gradient(135deg, #409eff 0%, #36cfc9 100%);
  color: white;
}

.login-container .login-box .login-header h1 {
  margin: 0 0 10px;
  font-size: 24px;
  font-weight: 600;
}

.login-container .login-box .login-header p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.login-container .login-box .login-form {
  padding: 30px;
}

.login-container .login-box .login-form h2 {
  margin: 0 0 30px;
  text-align: center;
  color: #333;
  font-size: 20px;
  font-weight: 500;
}

.login-container .login-box .login-form .form .login-btn {
  width: 100%;
  margin-top: 10px;
}

.login-container .login-box .login-form .login-actions {
  text-align: center;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.login-container .login-box .login-form .login-actions .el-link {
  font-size: 14px;
}

.login-container .login-box .login-form .login-tips {
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 6px;
  font-size: 12px;
  color: #666;
}

.login-container .login-box .login-form .login-tips p {
  margin: 5px 0;
}

.login-container .login-box .login-form .login-tips p:first-child {
  font-weight: 500;
  margin-bottom: 8px;
}

.login-container .login-box .login-footer {
  padding: 20px;
  text-align: center;
  border-top: 1px solid #e6e6e6;
  font-size: 12px;
  color: #999;
}

@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }

  .login-container .login-box {
    max-width: 100%;
  }

  .login-container .login-box .login-header h1 {
    font-size: 20px;
  }
}
</style>