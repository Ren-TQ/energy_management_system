<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <div class="stats-row">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="stat in stats" :key="stat.title">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-content">
              <div class="stat-icon" :style="{ color: stat.color }">
                <component :is="stat.icon" />
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stat.value }}</div>
                <div class="stat-title">{{ stat.title }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="chart-row">
      <el-row :gutter="20">
        <!-- 功率趋势图 -->
        <el-col :xs="24" :lg="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>设备功率实时监控</span>
                <div class="chart-actions">
                  <el-select
                    v-model="selectedMeter"
                    placeholder="选择设备"
                    size="small"
                    style="width: 200px; margin-right: 10px;"
                  >
                    <el-option
                      v-for="meter in meters"
                      :key="meter.id"
                      :label="`${meter.deviceName} (${meter.serialNumber})`"
                      :value="meter.id"
                    />
                  </el-select>
                  <el-button
                    type="primary"
                    size="small"
                    :icon="Refresh"
                    @click="refreshPowerChart"
                  >
                    刷新
                  </el-button>
                </div>
              </div>
            </template>
            <PowerChart
              :meter-id="selectedMeter"
              :limit="10"
              :auto-refresh="true"
            />
          </el-card>
        </el-col>

        <!-- 能耗分布图 -->
        <el-col :xs="24" :lg="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <span>今日能耗分布</span>
            </template>
            <EnergyChart />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 设备状态 -->
    <div class="device-row">
      <el-row :gutter="20">
        <el-col :xs="24">
          <el-card shadow="never" class="device-card">
            <template #header>
              <span>设备状态概览</span>
            </template>
            <el-table
              :data="deviceStatus"
              v-loading="loading"
              style="width: 100%"
            >
              <el-table-column prop="deviceName" label="设备名称" width="180" />
              <el-table-column prop="serialNumber" label="序列号" width="150" />
              <el-table-column prop="buildingName" label="所在建筑" width="120" />
              <el-table-column prop="roomNumber" label="房间号" width="100" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.status === 'ONLINE' ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ scope.row.status === 'ONLINE' ? '在线' : '离线' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="latestPower" label="当前功率(W)" width="120">
                <template #default="scope">
                  {{ scope.row.latestPower || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="voltage" label="电压(V)" width="100">
                <template #default="scope">
                  {{ scope.row.voltage || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="lastUpdate" label="最后更新" width="180">
                <template #default="scope">
                  {{ formatTime(scope.row.lastUpdate) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button
                    type="primary"
                    link
                    @click="viewMeterDetail(scope.row.id)"
                  >
                    详情
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 最近告警 -->
    <div class="alert-row">
      <el-row :gutter="20">
        <el-col :xs="24">
          <el-card shadow="never" class="alert-card">
            <template #header>
              <div class="alert-header">
                <span>最近告警</span>
                <el-button
                  type="primary"
                  link
                  @click="$router.push('/alerts')"
                >
                  查看更多
                </el-button>
              </div>
            </template>
            <el-table
              :data="recentAlerts"
              v-loading="alertsLoading"
              style="width: 100%"
            >
              <el-table-column prop="serialNumber" label="设备SN" width="150" />
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
              <el-table-column prop="alertValue" label="告警数值" width="120">
                <template #default="scope">
                  {{ scope.row.alertValue }}
                </template>
              </el-table-column>
              <el-table-column prop="alertDetail" label="详情" />
              <el-table-column prop="triggerTime" label="触发时间" width="180">
                <template #default="scope">
                  {{ formatDateTime(scope.row.triggerTime) }}
                </template>
              </el-table-column>
              <el-table-column prop="isResolved" label="状态" width="100">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.isResolved ? 'success' : 'danger'"
                    size="small"
                  >
                    {{ scope.row.isResolved ? '已处理' : '未处理' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120">
                <template #default="scope">
                  <el-button
                    v-if="!scope.row.isResolved"
                    type="primary"
                    link
                    @click="handleResolveAlert(scope.row.id)"
                  >
                    处理
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'
import { formatTime, formatDateTime } from '@/utils/date'
import PowerChart from '@/components/charts/PowerChart.vue'
import EnergyChart from '@/components/charts/EnergyChart.vue'

const router = useRouter()

const stats = ref([
  { title: '在线设备', value: '0', icon: 'Monitor', color: '#67c23a' },
  { title: '今日告警', value: '0', icon: 'Warning', color: '#e6a23c' },
  { title: '总能耗(kWh)', value: '0', icon: 'Lightning', color: '#409eff' },
  { title: '今日电费(元)', value: '0', icon: 'Money', color: '#f56c6c' }
])

const meters = ref([])
const selectedMeter = ref('')
const deviceStatus = ref([])
const recentAlerts = ref([])

const loading = ref(false)
const alertsLoading = ref(false)

let refreshTimer = null

// 加载统计数据
async function loadStats() {
  try {
    // 获取设备列表
    const metersData = await api.meter.getAll()
    const onlineMeters = metersData.content.filter(m => m.status === 'ONLINE')

    // 获取今日电费
    const cost = await api.energyData.getTodayCost()

    // 获取未处理告警数量
    const alertCount = await api.alert.getUnresolvedCount()

    stats.value[0].value = `${onlineMeters.length}/${metersData.content.length}`
    stats.value[1].value = alertCount.toString()
    stats.value[2].value = '--'  // 可以调用相关接口获取
    stats.value[3].value = cost ? cost.toFixed(2) : '0.00'

  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载设备列表
async function loadMeters() {
  try {
    loading.value = true
    const response = await api.meter.getAll()
    meters.value = response.content

    if (meters.value.length > 0 && !selectedMeter.value) {
      selectedMeter.value = meters.value[0].id
    }

    // 加载设备状态
    await loadDeviceStatus()

  } catch (error) {
    console.error('加载设备列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 加载设备状态
async function loadDeviceStatus() {
  const statusData = []

  for (const meter of meters.value.slice(0, 10)) { // 只显示前10个
    try {
      const latestData = await api.energyData.getLatest(meter.id)

      statusData.push({
        id: meter.id,
        deviceName: meter.deviceName,
        serialNumber: meter.serialNumber,
        buildingName: meter.buildingName,
        roomNumber: meter.roomNumber,
        status: meter.status,
        latestPower: latestData?.power,
        voltage: latestData?.voltage,
        lastUpdate: latestData?.timestamp
      })
    } catch (error) {
      console.error(`加载设备${meter.serialNumber}数据失败:`, error)
    }
  }

  deviceStatus.value = statusData
}

// 加载最近告警
async function loadRecentAlerts() {
  try {
    alertsLoading.value = true
    const response = await api.alert.getList({
      page: 0,
      size: 5
    })

    recentAlerts.value = response.content

  } catch (error) {
    console.error('加载告警列表失败:', error)
  } finally {
    alertsLoading.value = false
  }
}

// 刷新功率图表
function refreshPowerChart() {
  // PowerChart组件会监听selectedMeter变化
  // 这里可以添加其他刷新逻辑
  loadDeviceStatus()
}

// 查看设备详情
function viewMeterDetail(meterId) {
  router.push(`/meters?detail=${meterId}`)
}

// 处理告警
async function handleResolveAlert(alertId) {
  try {
    await api.alert.resolve(alertId)
    ElMessage.success('告警已处理')
    loadRecentAlerts()
    loadStats() // 重新加载统计
  } catch (error) {
    ElMessage.error('处理告警失败')
  }
}

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
  const textMap = {
    'POWER_OVERLOAD': '功率超限',
    'VOLTAGE_ABNORMAL': '电压异常',
    'DEVICE_OFFLINE': '设备离线'
  }
  return textMap[alertType] || '未知告警'
}

// 初始加载
onMounted(async () => {
  await loadStats()
  await loadMeters()
  await loadRecentAlerts()

  // 每30秒刷新一次数据
  refreshTimer = setInterval(async () => {
    await loadStats()
    await loadDeviceStatus()
  }, 30000)
})

// 清理定时器
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style>
.dashboard-container {
  padding: 20px;
}

.dashboard-container .stats-row {
  margin-bottom: 20px;
}

.dashboard-container .stats-row .stat-card {
  margin-bottom: 20px;
}

.dashboard-container .stats-row .stat-card .stat-content {
  display: flex;
  align-items: center;
}

.dashboard-container .stats-row .stat-card .stat-content .stat-icon {
  font-size: 36px;
  margin-right: 15px;
}

.dashboard-container .stats-row .stat-card .stat-content .stat-info {
  flex: 1;
}

.dashboard-container .stats-row .stat-card .stat-content .stat-info .stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 5px;
  color: #333;
}

.dashboard-container .stats-row .stat-card .stat-content .stat-info .stat-title {
  font-size: 14px;
  color: #999;
}

.dashboard-container .chart-row {
  margin-bottom: 20px;
}

.dashboard-container .chart-row .chart-card {
  height: 400px;
  display: flex;
  flex-direction: column;
}

.dashboard-container .chart-row .chart-card .el-card__header {
  padding: 15px 20px;
  border-bottom: 1px solid #e6e6e6;
}

.dashboard-container .chart-row .chart-card .el-card__body {
  flex: 1;
  padding: 20px;
}

.dashboard-container .chart-row .chart-card .chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dashboard-container .chart-row .chart-card .chart-header .chart-actions {
  display: flex;
  align-items: center;
}

.dashboard-container .device-row,
.dashboard-container .alert-row {
  margin-bottom: 20px;
}

.dashboard-container .device-row .el-card__header,
.dashboard-container .alert-row .el-card__header {
  padding: 15px 20px;
  border-bottom: 1px solid #e6e6e6;
}

.dashboard-container .device-row .el-card__body,
.dashboard-container .alert-row .el-card__body {
  padding: 0;
}

.dashboard-container .alert-row .alert-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;
  }

  .dashboard-container .chart-row .chart-card {
    height: 350px;
  }

  .dashboard-container .chart-row .chart-card .chart-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .dashboard-container .chart-row .chart-card .chart-header .chart-actions {
    margin-top: 10px;
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
