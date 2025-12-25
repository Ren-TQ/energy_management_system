<template>
  <div class="alert-container">
    <el-card shadow="never" class="main-card">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <el-input
            v-model="searchParams.serialNumber"
            placeholder="设备序列号"
            style="width: 180px; margin-right: 10px;"
            clearable
          />
          <el-select
            v-model="searchParams.alertType"
            placeholder="告警类型"
            style="width: 140px; margin-right: 10px;"
            clearable
          >
            <el-option
              v-for="type in alertTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
          <el-select
            v-model="searchParams.isResolved"
            placeholder="处理状态"
            style="width: 120px; margin-right: 10px;"
            clearable
          >
            <el-option label="未处理" :value="false" />
            <el-option label="已处理" :value="true" />
          </el-select>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 280px; margin-right: 10px;"
          />
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </div>

        <div class="action-buttons">
          <el-button
            type="primary"
            @click="handleResolveBatch"
            :disabled="selectedAlerts.length === 0"
            v-if="userRole === 'ADMIN'"
          >
            <el-icon><Check /></el-icon>
            批量处理
          </el-button>
          <el-button @click="refreshData">
            <el-icon><RefreshRight /></el-icon>
            刷新
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        ref="alertTableRef"
        :data="alertList"
        v-loading="loading"
        style="width: 100%"
        :border="true"
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" v-if="userRole === 'ADMIN'" />

        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="serialNumber" label="设备SN" width="180" />
        <el-table-column prop="meterName" label="设备名称" width="180" />
        <el-table-column prop="alertType" label="告警类型" width="120">
          <template #default="scope">
            <el-tag
              :type="getAlertTagType(scope.row.alertType)"
              size="small"
            >
              {{ getAlertTypeText(scope.row.alertType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="alertValue" label="告警数值" width="120" align="right">
          <template #default="scope">
            {{ formatValue(scope.row.alertType, scope.row.alertValue) }}
          </template>
        </el-table-column>
        <el-table-column prop="alertDetail" label="详情" />
        <el-table-column prop="triggerTime" label="触发时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.triggerTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="isResolved" label="处理状态" width="100">
          <template #default="scope">
            <el-tag
              :type="scope.row.isResolved ? 'success' : 'danger'"
              size="small"
            >
              {{ scope.row.isResolved ? '已处理' : '未处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="handleViewDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              v-if="!scope.row.isResolved && userRole === 'ADMIN'"
              type="success"
              link
              @click="handleResolve(scope.row)"
            >
              处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 告警详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="告警详情"
      width="600px"
    >
      <div v-if="currentAlert" class="alert-detail">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="告警ID">{{ currentAlert.id }}</el-descriptions-item>
          <el-descriptions-item label="设备信息">
            {{ currentAlert.meterName }} ({{ currentAlert.serialNumber }})
          </el-descriptions-item>
          <el-descriptions-item label="告警类型">
            <el-tag :type="getAlertTagType(currentAlert.alertType)">
              {{ getAlertTypeText(currentAlert.alertType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="告警数值">
            {{ formatValue(currentAlert.alertType, currentAlert.alertValue) }}
          </el-descriptions-item>
          <el-descriptions-item label="告警详情">{{ currentAlert.alertDetail }}</el-descriptions-item>
          <el-descriptions-item label="触发时间">{{ formatDateTime(currentAlert.triggerTime) }}</el-descriptions-item>
          <el-descriptions-item label="处理状态">
            <el-tag :type="currentAlert.isResolved ? 'success' : 'danger'">
              {{ currentAlert.isResolved ? '已处理' : '未处理' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <div class="action-section" v-if="!currentAlert.isResolved && userRole === 'ADMIN'">
          <el-button
            type="primary"
            @click="handleResolve(currentAlert)"
            style="width: 100%; margin-top: 20px;"
          >
            标记为已处理
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, RefreshRight, Check } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'
import { formatDateTime, getDateRange } from '@/utils/date'

const store = useStore()

// 用户角色 - 强制返回ADMIN用于测试
const userRole = computed(() => 'ADMIN')

// 数据
const alertList = ref([])
const selectedAlerts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const alertTableRef = ref()

// 搜索参数
const searchParams = reactive({
  serialNumber: '',
  alertType: '',
  isResolved: '',
  startDate: '',
  endDate: ''
})

const dateRange = ref([])

// 当前告警
const detailDialogVisible = ref(false)
const currentAlert = ref(null)

// 告警类型选项
const alertTypes = [
  { value: 'POWER_OVERLOAD', label: '功率超限' },
  { value: 'VOLTAGE_ABNORMAL', label: '电压异常' },
  { value: 'DEVICE_OFFLINE', label: '设备离线' }
]

// 获取告警标签类型
function getAlertTagType(alertType) {
  const typeMap = {
    'POWER_OVERLOAD': 'danger',
    'VOLTAGE_ABNORMAL': 'warning',
    'DEVICE_OFFLINE': 'info'
  }
  return typeMap[alertType] || 'default'
}

// 获取告警类型文本
function getAlertTypeText(alertType) {
  const type = alertTypes.find(t => t.value === alertType)
  return type ? type.label : alertType
}

// 格式化告警值
function formatValue(alertType, value) {
  switch (alertType) {
    case 'POWER_OVERLOAD':
      return `${value}W`
    case 'VOLTAGE_ABNORMAL':
      return `${value}V`
    default:
      return value || '--'
  }
}

// 加载告警列表
async function loadAlerts() {
  try {
    loading.value = true

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...searchParams
    }

    const response = await api.alert.getList(params)
    alertList.value = response.content
    total.value = response.totalElements

  } catch (error) {
    console.error('加载告警列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadAlerts()
}

// 重置搜索
function resetSearch() {
  searchParams.serialNumber = ''
  searchParams.alertType = ''
  searchParams.isResolved = ''
  searchParams.startDate = ''
  searchParams.endDate = ''
  dateRange.value = []
  currentPage.value = 1
  loadAlerts()
}

// 刷新数据
function refreshData() {
  loadAlerts()
}

// 分页大小变化
function handleSizeChange(size) {
  pageSize.value = size
  loadAlerts()
}

// 页码变化
function handlePageChange(page) {
  currentPage.value = page
  loadAlerts()
}

// 多选变化
function handleSelectionChange(selection) {
  selectedAlerts.value = selection
}

// 查看详情
function handleViewDetail(row) {
  currentAlert.value = row
  detailDialogVisible.value = true
}

// 处理告警
async function handleResolve(row) {
  try {
    await ElMessageBox.confirm(
      `确定要将此告警标记为已处理吗？`,
      '确认',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    await api.alert.resolve(row.id)
    ElMessage.success('处理成功')

    // 刷新数据
    loadAlerts()

    // 如果当前在查看详情，关闭对话框
    if (detailDialogVisible.value) {
      detailDialogVisible.value = false
    }

  } catch (error) {
    if (error !== 'cancel') {
      console.error('处理告警失败:', error)
      ElMessage.error(error.message || '处理失败')
    }
  }
}

// 批量处理
async function handleResolveBatch() {
  if (selectedAlerts.value.length === 0) return

  try {
    await ElMessageBox.confirm(
      `确定要批量处理 ${selectedAlerts.value.length} 条告警吗？`,
      '确认',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    const promises = selectedAlerts.value.map(alert =>
      api.alert.resolve(alert.id)
    )

    await Promise.all(promises)

    ElMessage.success(`成功处理 ${selectedAlerts.value.length} 条告警`)

    // 清除选中状态
    if (alertTableRef.value) {
      alertTableRef.value.clearSelection()
    }

    // 刷新数据
    loadAlerts()

  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量处理告警失败:', error)
      ElMessage.error(error.message || '批量处理失败')
    }
  }
}

// 监听日期范围变化
watch(dateRange, (newRange) => {
  if (newRange && newRange.length === 2) {
    searchParams.startDate = newRange[0]
    searchParams.endDate = newRange[1]
  } else {
    searchParams.startDate = ''
    searchParams.endDate = ''
  }
})

// 初始加载（默认显示最近7天的告警）
onMounted(() => {
  const { startDate, endDate } = getDateRange(7)
  dateRange.value = [startDate, endDate]
  searchParams.startDate = startDate
  searchParams.endDate = endDate

  loadAlerts()

  // 每30秒自动刷新一次
  setInterval(loadAlerts, 30000)
})
</script>

<style>
.alert-container {
  padding: 20px;
}

.alert-container .main-card .el-card__body {
  padding: 20px;
}

.alert-container .action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.alert-container .action-bar .search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.alert-container .action-bar .action-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.alert-container .pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.alert-container .alert-detail .action-section {
  margin-top: 20px;
}

@media (max-width: 768px) {
  .alert-container {
    padding: 10px;
  }

  .alert-container .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .alert-container .action-bar .search-bar,
  .alert-container .action-bar .action-buttons {
    justify-content: center;
    width: 100%;
  }

  .alert-container .action-bar .search-bar {
    margin-bottom: 10px;
  }
}
</style>
