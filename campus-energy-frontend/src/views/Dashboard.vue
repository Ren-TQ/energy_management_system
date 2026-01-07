<!-- ============================================ -->
<!-- MVVM架构 - View层 (视图层)                  -->
<!-- 职责：负责UI展示和用户交互界面               -->
<!-- ============================================ -->
<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="card-box stat-card">
          <el-icon :size="40" color="#409eff"><OfficeBuilding /></el-icon>
          <div class="stat-value">{{ stats.buildingCount || 0 }}</div>
          <div class="stat-label">建筑总数</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card-box stat-card success">
          <el-icon :size="40" color="#67c23a"><Cpu /></el-icon>
          <div class="stat-value">{{ stats.onlineDeviceCount || 0 }} / {{ stats.deviceCount || 0 }}</div>
          <div class="stat-label">在线设备 / 总设备</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card-box stat-card warning">
          <el-icon :size="40" color="#e6a23c"><Lightning /></el-icon>
          <div class="stat-value">{{ (stats.todayTotalEnergy || 0).toFixed(2) }}</div>
          <div class="stat-label">今日用电量 (kWh)</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="card-box stat-card danger">
          <el-icon :size="40" color="#f56c6c"><Bell /></el-icon>
          <div class="stat-value">{{ stats.unresolvedAlertCount || 0 }}</div>
          <div class="stat-label">未处理告警</div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <div class="card-box">
          <div class="card-title">各建筑用电量统计</div>
          <div class="chart-container" ref="buildingChartRef"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="card-box">
          <div class="card-title">告警类型分布</div>
          <div class="chart-container" ref="alertChartRef"></div>
        </div>
      </el-col>
    </el-row>
    
    <!-- 设备实时数据 & 最近告警 -->
    <el-row :gutter="20">
      <el-col :span="14">
        <div class="card-box">
          <div class="card-title">设备实时数据</div>
          <el-table :data="latestEnergyData" max-height="350" stripe>
            <el-table-column prop="deviceName" label="设备名称" width="150" />
            <el-table-column prop="voltage" label="电压(V)" width="100">
              <template #default="{ row }">
                <span :class="getVoltageClass(row.voltage)">{{ row.voltage?.toFixed(1) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="current" label="电流(A)" width="100">
              <template #default="{ row }">{{ row.current?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="power" label="功率(W)" width="100">
              <template #default="{ row }">
                <span :class="{ 'text-danger': row.isAbnormal }">{{ row.power?.toFixed(1) }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="totalEnergy" label="累计(kWh)" width="120">
              <template #default="{ row }">{{ row.totalEnergy?.toFixed(3) }}</template>
            </el-table-column>
            <el-table-column prop="collectTime" label="采集时间">
              <template #default="{ row }">{{ formatTime(row.collectTime) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="card-box">
          <div class="card-title">最近告警</div>
          <el-table :data="recentAlerts" max-height="350" stripe>
            <el-table-column prop="alertTypeLabel" label="类型" width="100">
              <template #default="{ row }">
                <el-tag :type="getAlertTagType(row.alertType)" size="small">
                  {{ row.alertTypeLabel }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="deviceName" label="设备" width="120" />
            <el-table-column prop="triggerTime" label="时间">
              <template #default="{ row }">{{ formatTime(row.triggerTime) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.isResolved ? 'success' : 'danger'" size="small">
                  {{ row.isResolved ? '已处理' : '未处理' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<!-- ============================================ -->
<!-- MVVM架构 - ViewModel层 (视图模型层)         -->
<!-- 职责：处理业务逻辑、状态管理、数据绑定       -->
<!-- ============================================ -->
<script setup>
// 导入Vue核心功能
import { ref, onMounted, onUnmounted } from 'vue'
// 导入图表库
import * as echarts from 'echarts'
// 导入日期处理库
import dayjs from 'dayjs'
// ============================================
// MVVM架构 - Model层 (数据模型层)
// 职责：与后端API通信，获取和提交数据
// 位置：@/api/statistics.js, @/api/energy.js, @/api/alert.js
// ============================================
import { getOverviewStatistics } from '@/api/statistics'
import { getLatestAllEnergyData } from '@/api/energy'
import { getRecentAlerts } from '@/api/alert'

const stats = ref({})
const latestEnergyData = ref([])
const recentAlerts = ref([])

const buildingChartRef = ref(null)
const alertChartRef = ref(null)
let buildingChart = null
let alertChart = null
let refreshTimer = null

// 加载统计数据
async function loadStats() {
  try {
    const res = await getOverviewStatistics()
    stats.value = res.data
    
    // 更新图表
    updateBuildingChart(res.data.buildingEnergyStats || [])
    updateAlertChart(res.data.alertTypeStats || {})
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载设备实时数据
async function loadLatestEnergyData() {
  try {
    const res = await getLatestAllEnergyData()
    latestEnergyData.value = res.data || []
  } catch (error) {
    console.error('加载能耗数据失败:', error)
  }
}

// 加载最近告警
async function loadRecentAlerts() {
  try {
    const res = await getRecentAlerts()
    recentAlerts.value = res.data || []
  } catch (error) {
    console.error('加载告警数据失败:', error)
  }
}

// 初始化建筑用电量图表
function initBuildingChart() {
  if (!buildingChartRef.value) return
  buildingChart = echarts.init(buildingChartRef.value)
}

// 更新建筑用电量图表
function updateBuildingChart(data) {
  if (!buildingChart) return
  
  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.buildingName),
      axisLabel: { rotate: 30 }
    },
    yAxis: {
      type: 'value',
      name: '用电量(kWh)'
    },
    series: [{
      name: '用电量',
      type: 'bar',
      data: data.map(item => item.totalEnergy),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#409eff' },
          { offset: 1, color: '#79bbff' }
        ])
      },
      barWidth: '50%'
    }]
  }
  
  buildingChart.setOption(option)
}

// 初始化告警类型图表
function initAlertChart() {
  if (!alertChartRef.value) return
  alertChart = echarts.init(alertChartRef.value)
}

// 更新告警类型图表
function updateAlertChart(data) {
  if (!alertChart) return
  
  const pieData = Object.entries(data).map(([name, value]) => ({ name, value }))
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '10%',
      top: 'center'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 8,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false
      },
      emphasis: {
        label: {
          show: true,
          fontSize: 14,
          fontWeight: 'bold'
        }
      },
      data: pieData.length > 0 ? pieData : [{ name: '暂无数据', value: 1 }]
    }]
  }
  
  alertChart.setOption(option)
}

// 格式化时间
function formatTime(time) {
  if (!time) return '-'
  return dayjs(time).format('MM-DD HH:mm:ss')
}

// 电压样式
function getVoltageClass(voltage) {
  if (voltage < 198 || voltage > 242) return 'text-danger'
  if (voltage < 210 || voltage > 235) return 'text-warning'
  return ''
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

// 加载所有数据
function loadAllData() {
  loadStats()
  loadLatestEnergyData()
  loadRecentAlerts()
}

// 窗口大小变化时重置图表
function handleResize() {
  buildingChart?.resize()
  alertChart?.resize()
}

onMounted(() => {
  initBuildingChart()
  initAlertChart()
  loadAllData()
  
  // 每10秒刷新数据
  refreshTimer = setInterval(loadAllData, 10000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  window.removeEventListener('resize', handleResize)
  buildingChart?.dispose()
  alertChart?.dispose()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-row {
    margin-bottom: 20px;
  }
  
  .stat-card {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 24px;
    
    .el-icon {
      margin-bottom: 12px;
    }
  }
  
  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }
  
  .text-danger {
    color: #f56c6c;
    font-weight: bold;
  }
  
  .text-warning {
    color: #e6a23c;
  }
}
</style>

