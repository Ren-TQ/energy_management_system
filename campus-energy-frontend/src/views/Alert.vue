<!-- ============================================ -->
<!-- MVVM架构 - View层 (视图层)                  -->
<!-- 职责：负责UI展示和用户交互界面               -->
<!-- ============================================ -->
<template>
  <div class="alert-page">
    <div class="card-box">
      <div class="page-header">
        <h2 class="page-title">告警管理</h2>
        <div class="header-stats">
          <el-tag type="danger" size="large">
            未处理: {{ unresolvedCount }}
          </el-tag>
          <el-tag type="warning" size="large">
            今日告警: {{ todayCount }}
          </el-tag>
        </div>
      </div>
      
      <!-- 筛选 -->
      <div class="search-bar">
        <el-form :inline="true">
          <el-form-item label="告警类型">
            <el-select v-model="filterType" placeholder="全部" clearable>
              <el-option label="功率过载" value="POWER_OVERLOAD" />
              <el-option label="电压过高" value="VOLTAGE_HIGH" />
              <el-option label="电压过低" value="VOLTAGE_LOW" />
              <el-option label="电流异常" value="CURRENT_ABNORMAL" />
              <el-option label="设备离线" value="DEVICE_OFFLINE" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filterResolved" placeholder="全部" clearable>
              <el-option label="未处理" :value="false" />
              <el-option label="已处理" :value="true" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadData">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 数据表格 -->
      <el-table :data="filteredData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="alertTypeLabel" label="告警类型" width="110">
          <template #default="{ row }">
            <el-tag :type="getAlertTagType(row.alertType)" effect="dark">
              {{ row.alertTypeLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceName" label="设备" width="140" />
        <el-table-column prop="buildingName" label="建筑" width="130" />
        <el-table-column prop="roomNumber" label="房间" width="110" />
        <el-table-column label="告警数值" width="150">
          <template #default="{ row }">
            <span class="alert-value">{{ row.alertValue?.toFixed(2) }}</span>
            <span class="threshold"> / {{ row.thresholdValue?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="详情" show-overflow-tooltip />
        <el-table-column prop="triggerTime" label="触发时间" width="170">
          <template #default="{ row }">{{ formatTime(row.triggerTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isResolved ? 'success' : 'danger'">
              {{ row.isResolved ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="userStore.isAdmin" label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.isResolved"
              type="primary"
              link
              @click="openResolveDialog(row)"
            >
              处理
            </el-button>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-box">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </div>
    
    <!-- 处理告警对话框 -->
    <el-dialog v-model="resolveDialogVisible" title="处理告警" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="告警类型">{{ currentAlert.alertTypeLabel }}</el-descriptions-item>
        <el-descriptions-item label="设备">{{ currentAlert.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="告警详情">{{ currentAlert.description }}</el-descriptions-item>
        <el-descriptions-item label="触发时间">{{ formatTime(currentAlert.triggerTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <el-form style="margin-top: 20px">
        <el-form-item label="处理备注">
          <el-input
            v-model="resolveNote"
            type="textarea"
            :rows="3"
            placeholder="请输入处理备注（可选）"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="resolveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="resolving" @click="handleResolve">
          确认处理
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<!-- ============================================ -->
<!-- MVVM架构 - ViewModel层 (视图模型层)         -->
<!-- 职责：处理业务逻辑、状态管理、数据绑定       -->
<!-- ============================================ -->
<script setup>
// 导入Vue核心功能
import { ref, reactive, computed, onMounted } from 'vue'
// 导入UI组件库
import { ElMessage } from 'element-plus'
// 导入日期处理库
import dayjs from 'dayjs'
// 导入状态管理（Pinia Store）
import { useUserStore } from '@/stores/user'
// ============================================
// MVVM架构 - Model层 (数据模型层)
// 职责：与后端API通信，获取和提交数据
// 位置：@/api/alert.js
// ============================================
import { getAlerts, resolveAlert, getTodayAlertCount, getUnresolvedAlertCount } from '@/api/alert'

const userStore = useUserStore()

const loading = ref(false)
const resolving = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

const filterType = ref('')
const filterResolved = ref('')

const resolveDialogVisible = ref(false)
const currentAlert = ref({})
const resolveNote = ref('')

const todayCount = ref(0)
const unresolvedCount = ref(0)

// 过滤后的数据
const filteredData = computed(() => {
  let data = tableData.value
  if (filterType.value) {
    data = data.filter(item => item.alertType === filterType.value)
  }
  if (filterResolved.value !== '') {
    data = data.filter(item => item.isResolved === filterResolved.value)
  }
  return data
})

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getAlerts({
      page: currentPage.value - 1,
      size: pageSize.value,
      sort: 'triggerTime,desc'
    })
    tableData.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载统计
async function loadStats() {
  try {
    const [todayRes, unresolvedRes] = await Promise.all([
      getTodayAlertCount(),
      getUnresolvedAlertCount()
    ])
    todayCount.value = todayRes.data || 0
    unresolvedCount.value = unresolvedRes.data || 0
  } catch (error) {
    console.error('加载统计失败:', error)
  }
}

// 告警标签类型
function getAlertTagType(type) {
  const typeMap = {
    'POWER_OVERLOAD': 'danger',
    'VOLTAGE_HIGH': 'warning',
    'VOLTAGE_LOW': 'warning',
    'CURRENT_ABNORMAL': 'info',
    'DEVICE_OFFLINE': ''
  }
  return typeMap[type] || 'info'
}

// 格式化时间
function formatTime(time) {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 打开处理对话框
function openResolveDialog(row) {
  currentAlert.value = row
  resolveNote.value = ''
  resolveDialogVisible.value = true
}

// 处理告警
async function handleResolve() {
  resolving.value = true
  try {
    await resolveAlert(currentAlert.value.id, resolveNote.value)
    ElMessage.success('告警已处理')
    resolveDialogVisible.value = false
    loadData()
    loadStats()
  } catch (error) {
    console.error('处理失败:', error)
  } finally {
    resolving.value = false
  }
}

onMounted(() => {
  loadData()
  loadStats()
})
</script>

<style lang="scss" scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .page-title {
    margin: 0;
  }
  
  .header-stats {
    display: flex;
    gap: 12px;
  }
}

.alert-value {
  color: #f56c6c;
  font-weight: bold;
}

.threshold {
  color: #909399;
  font-size: 12px;
}

.text-muted {
  color: #c0c4cc;
}

.pagination-box {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

