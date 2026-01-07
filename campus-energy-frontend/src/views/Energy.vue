<template>
  <div class="energy-page">
    <div class="card-box">
      <div class="page-header">
        <h2 class="page-title">能耗数据</h2>
      </div>
      
      <!-- 筛选 -->
      <div class="search-bar">
        <el-form :inline="true">
          <el-form-item label="选择设备">
            <el-select
              v-model="selectedDevice"
              placeholder="请选择设备"
              filterable
              @change="handleDeviceChange"
            >
              <el-option
                v-for="item in devices"
                :key="item.id"
                :label="`${item.name} (${item.serialNumber})`"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :disabled="!selectedDevice" @click="loadEnergyData">
              刷新数据
            </el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 设备最新数据 -->
      <div v-if="latestData" class="latest-data-card">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="data-item">
              <div class="data-label">电压 (V)</div>
              <div class="data-value" :class="getVoltageClass(latestData.voltage)">
                {{ latestData.voltage?.toFixed(2) }}
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="data-item">
              <div class="data-label">电流 (A)</div>
              <div class="data-value">{{ latestData.current?.toFixed(2) }}</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="data-item">
              <div class="data-label">功率 (W)</div>
              <div class="data-value" :class="{ 'text-danger': latestData.isAbnormal }">
                {{ latestData.power?.toFixed(2) }}
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="data-item">
              <div class="data-label">累计用电量 (kWh)</div>
              <div class="data-value text-primary">{{ latestData.totalEnergy?.toFixed(3) }}</div>
            </div>
          </el-col>
        </el-row>
        <div class="update-time">
          最后更新: {{ formatTime(latestData.collectTime) }}
        </div>
      </div>
      
      <!-- 实时功率曲线 -->
      <div class="chart-section">
        <h3>今日功率曲线</h3>
        <div class="chart-container" ref="powerChartRef"></div>
      </div>
      
      <!-- 历史数据表格 -->
      <div class="table-section">
        <h3>历史数据</h3>
        <el-table :data="energyDataList" v-loading="loading" stripe border max-height="400">
          <el-table-column prop="collectTime" label="采集时间" width="180">
            <template #default="{ row }">{{ formatTime(row.collectTime) }}</template>
          </el-table-column>
          <el-table-column prop="voltage" label="电压(V)" width="120">
            <template #default="{ row }">
              <span :class="getVoltageClass(row.voltage)">{{ row.voltage?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="current" label="电流(A)" width="120">
            <template #default="{ row }">{{ row.current?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="power" label="功率(W)" width="120">
            <template #default="{ row }">
              <span :class="{ 'text-danger': row.isAbnormal }">{{ row.power?.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="totalEnergy" label="累计(kWh)" width="130">
            <template #default="{ row }">{{ row.totalEnergy?.toFixed(3) }}</template>
          </el-table-column>
          <el-table-column prop="isAbnormal" label="状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="row.isAbnormal ? 'danger' : 'success'" size="small">
                {{ row.isAbnormal ? '异常' : '正常' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import { getDevices } from '@/api/device'
import { getLatestEnergyData, getTodayEnergyData } from '@/api/energy'

const loading = ref(false)
const devices = ref([])
const selectedDevice = ref('')
const latestData = ref(null)
const energyDataList = ref([])

const powerChartRef = ref(null)
let powerChart = null
let refreshTimer = null

// 加载设备列表
async function loadDevices() {
  try {
    const res = await getDevices()
    devices.value = res.data || []
    if (devices.value.length > 0) {
      selectedDevice.value = devices.value[0].id
      loadEnergyData()
    }
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

// 设备选择变化
function handleDeviceChange() {
  loadEnergyData()
}

// 加载能耗数据
async function loadEnergyData() {
  if (!selectedDevice.value) return
  
  loading.value = true
  try {
    // 加载最新数据
    const latestRes = await getLatestEnergyData(selectedDevice.value)
    latestData.value = latestRes.data
    
    // 加载今日数据
    const todayRes = await getTodayEnergyData(selectedDevice.value)
    energyDataList.value = todayRes.data || []
    
    // 更新图表
    updatePowerChart(energyDataList.value)
  } catch (error) {
    console.error('加载能耗数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化功率图表
function initPowerChart() {
  if (!powerChartRef.value) return
  powerChart = echarts.init(powerChartRef.value)
}

// 更新功率图表
function updatePowerChart(data) {
  if (!powerChart) return
  
  // 按时间正序排列
  const sortedData = [...data].sort((a, b) => 
    new Date(a.collectTime) - new Date(b.collectTime)
  )
  
  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const item = params[0]
        return `${item.axisValue}<br/>功率: ${item.value} W`
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: sortedData.map(item => dayjs(item.collectTime).format('HH:mm:ss')),
      axisLabel: {
        rotate: 45,
        interval: Math.floor(sortedData.length / 10)
      }
    },
    yAxis: {
      type: 'value',
      name: '功率(W)'
    },
    series: [{
      name: '功率',
      type: 'line',
      data: sortedData.map(item => item.power?.toFixed(2)),
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.4)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      },
      lineStyle: { color: '#409eff', width: 2 },
      itemStyle: { color: '#409eff' }
    }]
  }
  
  powerChart.setOption(option)
}

// 格式化时间
function formatTime(time) {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 电压样式
function getVoltageClass(voltage) {
  if (voltage < 198 || voltage > 242) return 'text-danger'
  if (voltage < 210 || voltage > 235) return 'text-warning'
  return ''
}

// 窗口大小变化
function handleResize() {
  powerChart?.resize()
}

onMounted(() => {
  initPowerChart()
  loadDevices()
  
  // 每10秒刷新
  refreshTimer = setInterval(() => {
    if (selectedDevice.value) {
      loadEnergyData()
    }
  }, 10000)
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  window.removeEventListener('resize', handleResize)
  powerChart?.dispose()
})
</script>

<style lang="scss" scoped>
.page-header {
  margin-bottom: 20px;
  
  .page-title {
    margin: 0;
  }
}

.latest-data-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  color: #fff;
  
  .data-item {
    text-align: center;
    
    .data-label {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 8px;
    }
    
    .data-value {
      font-size: 28px;
      font-weight: bold;
    }
  }
  
  .update-time {
    text-align: right;
    margin-top: 16px;
    font-size: 12px;
    opacity: 0.8;
  }
  
  .text-danger {
    color: #ffb3b3 !important;
  }
  
  .text-warning {
    color: #ffe066 !important;
  }
  
  .text-primary {
    color: #a5d6ff !important;
  }
}

.chart-section,
.table-section {
  margin-top: 24px;
  
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 16px;
  }
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.text-warning {
  color: #e6a23c;
}
</style>

