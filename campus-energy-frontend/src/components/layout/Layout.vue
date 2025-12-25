<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="sidebarWidth" class="sidebar">
      <div class="logo">
        <h1>{{ collapsed ? '能耗' : '能耗监测系统' }}</h1>
      </div>

      <el-menu
        :default-active="$route.path"
        :collapse="store.state.sidebarCollapsed"
        router
        class="sidebar-menu"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>

        <el-menu-item index="/buildings">
          <el-icon><OfficeBuilding /></el-icon>
          <span>建筑管理</span>
        </el-menu-item>

        <el-menu-item index="/meters">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>

        <el-menu-item index="/alerts">
          <el-icon><Warning /></el-icon>
          <span>告警管理</span>
          <el-badge
            v-if="unresolvedCount > 0"
            :value="unresolvedCount"
            class="badge"
          />
        </el-menu-item>

        <el-menu-item index="/statistics">
          <el-icon><DataAnalysis /></el-icon>
          <span>统计分析</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon
            class="collapse-btn"
            @click="toggleSidebar"
          >
            <Expand v-if="store.state.sidebarCollapsed" />
            <Fold v-else />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">
              首页
            </el-breadcrumb-item>
            <el-breadcrumb-item>
              {{ $route.meta.title || '页面' }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="avatarUrl" />
              <span class="username">{{ store.getters.userName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>

      <!-- 页脚 -->
      <el-footer class="footer" height="40px">
        <div class="footer-content">
          <span>© 2025 智慧校园能耗监测系统</span>
          <span>版本: 1.0.0</span>
        </div>
      </el-footer>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { api } from '@/api/energyApi'

const store = useStore()
const router = useRouter()

const unresolvedCount = ref(0)
const avatarUrl = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png')

const collapsed = computed(() => store.state.sidebarCollapsed)
const sidebarWidth = computed(() => collapsed.value ? '64px' : '200px')

// 获取未处理告警数量
async function fetchUnresolvedCount() {
  try {
    const count = await api.alert.getUnresolvedCount()
    unresolvedCount.value = count
  } catch (error) {
    console.error('获取告警数量失败:', error)
  }
}

// 切换侧边栏
function toggleSidebar() {
  store.dispatch('toggleSidebar')
}

// 用户操作
function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 退出登录
async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })

    await api.auth.logout()
    store.dispatch('logout')
    router.push('/login')
    ElMessage.success('退出登录成功')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}

onMounted(() => {
  fetchUnresolvedCount()
  // 每30秒更新一次告警数量
  setInterval(fetchUnresolvedCount, 30000)
})
</script>

<style>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.layout-container .sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.layout-container .sidebar .logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #2a3a4f;
}

.layout-container .sidebar .logo h1 {
  color: #fff;
  font-size: 18px;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
}

.layout-container .sidebar .sidebar-menu {
  border-right: none;
  height: calc(100vh - 60px);
}

.layout-container .sidebar .sidebar-menu .el-menu-item {
  position: relative;
}

.layout-container .sidebar .sidebar-menu .el-menu-item .badge {
  position: absolute;
  top: 10px;
  right: 10px;
}

.layout-container .sidebar .sidebar-menu .el-menu-item .badge .el-badge__content {
  border: none;
}

.layout-container .main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.layout-container .main-container .header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  height: 60px;
}

.layout-container .main-container .header .header-left {
  display: flex;
  align-items: center;
}

.layout-container .main-container .header .header-left .collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
  color: #666;
}

.layout-container .main-container .header .header-left .collapse-btn:hover {
  color: #409eff;
}

.layout-container .main-container .header .header-left .breadcrumb {
  font-size: 14px;
}

.layout-container .main-container .header .header-right .user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
}

.layout-container .main-container .header .header-right .user-info:hover {
  background-color: #f5f7fa;
}

.layout-container .main-container .header .header-right .user-info .username {
  margin: 0 8px;
  font-size: 14px;
}

.layout-container .main-container .content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  background-color: #f0f2f5;
}

.layout-container .main-container .footer {
  background-color: #fff;
  border-top: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: center;
}

.layout-container .main-container .footer .footer-content {
  display: flex;
  justify-content: space-between;
  width: 100%;
  max-width: 1200px;
  color: #999;
  font-size: 12px;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .layout-container .sidebar {
    position: fixed;
    z-index: 1000;
    height: 100vh;
  }

  .layout-container .main-container {
    margin-left: 64px;
  }

  .layout-container .sidebar:not(.el-aside--collapse) {
    width: 200px !important;
  }

  .layout-container .main-container .header .header-left .breadcrumb {
    display: none;
  }
}
</style>
