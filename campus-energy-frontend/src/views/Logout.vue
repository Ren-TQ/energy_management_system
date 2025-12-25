<template>
  <div class="logout-container">
    <h2>正在退出登录...</h2>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import store from '@/store'

const router = useRouter()

onMounted(() => {
  // 清除认证信息
  store.dispatch('logout')
  
  // 跳转到登录页面
  router.push('/login')
})
</script>

<style scoped>
.logout-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}
</style>