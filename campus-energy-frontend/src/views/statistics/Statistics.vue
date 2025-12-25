<template>
  <div class="statistics-container">
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
        <!-- 能耗趋势图 -->
        <el-col :xs="24" :lg="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>能耗趋势分析</span>
                <div class="chart-actions">
                  <el-select
                    v-model="trendDays"
                    placeholder="选择天数"
                    size="small"
                    style="width: 100px;"
                  >
                    <el-option label="7天" :value="7" />
                    <el-option label="30天" :value="30" />
                    <el-option label="90天" :value="90" />
                  </el-select>
                  <el-button
                    type="primary"
                    size="small"
                    :icon="Refresh"
                    @click="loadEnergyTrend"
                    style="margin-left: 10px;"
                  >
                    刷新
                  </el-button>
                </div>
              </div>
            </template>
            <div id="energy-trend-chart" class="chart" v-loading="trendLoading"></div>
          </el-card>
        </el-col>

        <!-- 建筑能耗排行 -->
        <el-col :xs="24" :lg="12">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <span>建筑能耗排行</span>
            </template>
            <div id="building-rank-chart" class="chart" v-loading="rankLoading"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 告警统计 -->
    <div class="chart-row">
      <el-row :gutter="20">
        <el-col :xs="24">
          <el-card shadow="never" class="chart-card">
            <template #header>
              <div class="chart-header">
                <span>告警统计分析</span>
                <div class="chart-actions">
                  <el-select
                    v-model="alertStatDays"
                    placeholder="选择天数"
                    size="small"
                    style="width: 100px;"
                  >
                    <el-option label="7天" :value="7" />
                    <el-option label="30天" :value="30" />
                    <el-option label="90天" :value="90" />
                  </el-select>
                </div>
              </div>
            </template>
            <el-row :gutter="20">
              <el-col :xs="24" :md="8">
                <div class="alert-stat-card">
                  <div class="stat-title">告警类型分布</div>
                  <div id="alert-type-chart" class="small-chart"></div>
                </div>
              </el-col>
              <el-col :xs="24" :md="8">
                <div class="alert-stat-card">
                  <div class="stat-title">告警趋势</div>
                  <div id="alert-trend-chart" class="small-chart"></div>
                </div>
              </el-col>
              <el-col :xs="24" :md="8">
                <div class="alert-stat-card">
                  <div class="stat-title">处理状态</div>
                  <div id="alert-status-chart" class="small-chart"></div>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 数据汇总表格 -->
    <div class="table-row">
      <el-row :gutter="20">
        <el-col :xs="24">
          <el-card shadow="never" class="table-card">
            <template #header>
              <span>能耗数据汇总</span>
            </template>
            <el-table
              :data="summaryData"
              v-loading="summaryLoading"
              style="width: 100%"
              :border="true"
              stripe
            >
              <el-table-column prop="buildingName" label="建筑名称" width="180" />
              <el-table-column prop="meterCount" label="设备数量" width="100" align="center" />
              <el-table-column prop="todayEnergy" label="今日能耗(kWh)" width="120" align="right">
                <template #default="scope">
                  {{ scope.row.todayEnergy?.toFixed(2) || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="weekEnergy" label="本周能耗(kWh)" width="120" align="right">
                <template #default="scope">
                  {{ scope.row.weekEnergy?.toFixed(2) || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="monthEnergy" label="本月能耗(kWh)" width="120" align="right">
                <template #default="scope">
                  {{ scope.row.monthEnergy?.toFixed(2) || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="todayCost" label="今日电费(元)" width="120" align="right">
                <template #default="scope">
                  {{ scope.row.todayCost?.toFixed(2) || '--' }}
                </template>
              </el-table-column>
              <el-table-column prop="onlineRate" label="在线率" width="100" align="center">
                <template #default="scope">
                  <el-tag
                    :type="getOnlineRateTag(scope.row.onlineRate)"
                    size="small"
                  >
                    {{ scope.row.onlineRate }}%
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="alertCount" label="今日告警" width="100" align="center">
                <template #default="scope">
                  <el-tag
                    :type="scope.row.alertCount > 0 ? 'danger' : 'success'"
                    size="small"
                  >
                    {{ scope.row.alertCount }}
                  </el-tag>
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
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'
import { Refresh } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'

// 统计卡片数据
const stats = ref([
  { title: '总建筑数', value: '0', icon: 'OfficeBuilding', color: '#409eff' },
  { title: '总设备数', value: '0', icon: 'Monitor', color: '#67c23a' },
  { title: '在线设备', value: '0', icon: 'Connection', color: '#13c2c2' },
  { title: '本月总能耗', value: '0 kWh', icon: 'Lightning', color: '#f56c6c' }
])

// 图表相关
const trendDays = ref(7)
const alertStatDays = ref(7)
const trendLoading = ref(false)
const rankLoading = ref(false)
const summaryLoading = ref(false)

// 汇总数据
const summaryData = ref([])

// ECharts实例
let energyTrendChart = null
let buildingRankChart = null
let alertTypeChart = null
let alertTrendChart = null
let alertStatusChart = null

// 获取在线率标签类型
function getOnlineRateTag(rate) {
  if (rate >= 95) return 'success'
  if (rate >= 80) return 'warning'
  return 'danger'
}

// 加载统计数据
async function loadStats() {
  try {
    // 获取建筑和设备数据
    const buildings = await api.building.getAll()
    const meters = await api.meter.getAll()

    const onlineMeters = meters.content.filter(m => m.status === 'ONLINE')
    const onlineRate = meters.content.length > 0
      ? Math.round((onlineMeters.length / meters.content.length) * 100)
      : 0

    stats.value[0].value = buildings.length.toString()
    stats.value[1].value = meters.content.length.toString()
    stats.value[2].value = `${onlineMeters.length} (${onlineRate}%)`

  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 初始化ECharts图表
function initCharts() {
  // 能耗趋势图
  const trendChartDom = document.getElementById('energy-trend-chart')
  if (trendChartDom) {
    energyTrendChart = echarts.init(trendChartDom)

    // 模拟数据
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['能耗(kWh)', '电费(元)']
      },
      xAxis: {
        type: 'category',
        data: ['12-01', '12-02', '12-03', '12-04', '12-05', '12-06', '12-07']
      },
      yAxis: [
        {
          type: 'value',
          name: '能耗(kWh)'
        },
        {
          type: 'value',
          name: '电费(元)'
        }
      ],
      series: [
        {
          name: '能耗(kWh)',
          type: 'line',
          data: [1250.5, 1320.8, 1280.3, 1400.2, 1350.6, 1300.9, 1380.4],
          smooth: true,
          lineStyle: {
            color: '#409eff'
          }
        },
        {
          name: '电费(元)',
          type: 'line',
          yAxisIndex: 1,
          data: [1000.4, 1056.6, 1024.2, 1120.2, 1080.5, 1040.7, 1104.3],
          smooth: true,
          lineStyle: {
            color: '#f56c6c'
          }
        }
      ]
    }

    energyTrendChart.setOption(option)
  }

  // 建筑能耗排行
  const rankChartDom = document.getElementById('building-rank-chart')
  if (rankChartDom) {
    buildingRankChart = echarts.init(rankChartDom)

    const option = {
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      xAxis: {
        type: 'value',
        name: '能耗(kWh)'
      },
      yAxis: {
        type: 'category',
        data: ['宿舍三号楼', '力行楼', '软件学院楼', '图书馆', '办公楼']
      },
      series: [
        {
          name: '能耗',
          type: 'bar',
          data: [1250.5, 1320.8, 1280.3, 1400.2, 1350.6],
          itemStyle: {
            color: '#67c23a'
          }
        }
      ]
    }

    buildingRankChart.setOption(option)
  }

  // 告警类型分布
  const typeChartDom = document.getElementById('alert-type-chart')
  if (typeChartDom) {
    alertTypeChart = echarts.init(typeChartDom)

    const option = {
      tooltip: {
        trigger: 'item'
      },
      series: [
        {
          name: '告警类型',
          type: 'pie',
          radius: '70%',
          data: [
            { value: 35, name: '功率超限' },
            { value: 25, name: '电压异常' },
            { value: 15, name: '设备离线' },
            { value: 25, name: '其他' }
          ]
        }
      ]
    }

    alertTypeChart.setOption(option)
  }

  // 告警趋势
  const trendAlertChartDom = document.getElementById('alert-trend-chart')
  if (trendAlertChartDom) {
    alertTrendChart = echarts.init(trendAlertChartDom)

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      xAxis: {
        type: 'category',
        data: ['12-01', '12-02', '12-03', '12-04', '12-05', '12-06', '12-07'],
        show: false
      },
      yAxis: {
        type: 'value',
        show: false
      },
      series: [
        {
          name: '告警数量',
          type: 'line',
          data: [8, 12, 6, 15, 10, 8, 13],
          smooth: true
        }
      ]
    }

    alertTrendChart.setOption(option)
  }

  // 告警处理状态
  const statusChartDom = document.getElementById('alert-status-chart')
  if (statusChartDom) {
    alertStatusChart = echarts.init(statusChartDom)

    const option = {
      tooltip: {
        trigger: 'item'
      },
      series: [
        {
          name: '处理状态',
          type: 'pie',
          radius: '70%',
          data: [
            { value: 65, name: '已处理' },
            { value: 35, name: '未处理' }
          ]
        }
      ]
    }

    alertStatusChart.setOption(option)
  }
}

// 加载能耗趋势
function loadEnergyTrend() {
  trendLoading.value = true
  setTimeout(() => {
    trendLoading.value = false
  }, 500)
}

// 加载汇总数据
async function loadSummaryData() {
  try {
    summaryLoading.value = true

    // 模拟数据
    const mockData = [
      {
        buildingName: '宿舍三号楼',
        meterCount: 3,
        todayEnergy: 125.5,
        weekEnergy: 850.3,
        monthEnergy: 3200.8,
        todayCost: 100.4,
        onlineRate: 100,
        alertCount: 1
      },
      {
        buildingName: '力行楼',
        meterCount: 3,
        todayEnergy: 180.2,
        weekEnergy: 1250.6,
        monthEnergy: 4800.5,
        todayCost: 144.2,
        onlineRate: 100,
        alertCount: 0
      },
      {
        buildingName: '软件学院楼',
        meterCount: 2,
        todayEnergy: 220.8,
        weekEnergy: 1550.4,
        monthEnergy: 6200.3,
        todayCost: 176.6,
        onlineRate: 50,
        alertCount: 2
      },
      {
        buildingName: '图书馆',
        meterCount: 2,
        todayEnergy: 150.3,
        weekEnergy: 1050.7,
        monthEnergy: 4100.6,
        todayCost: 120.2,
        onlineRate: 100,
        alertCount: 0
      }
    ]

    summaryData.value = mockData

  } catch (error) {
    console.error('加载汇总数据失败:', error)
  } finally {
    summaryLoading.value = false
  }
}

// 监听窗口大小变化
function handleResize() {
  if (energyTrendChart) energyTrendChart.resize()
  if (buildingRankChart) buildingRankChart.resize()
  if (alertTypeChart) alertTypeChart.resize()
  if (alertTrendChart) alertTrendChart.resize()
  if (alertStatusChart) alertStatusChart.resize()
}

// 监听天数变化
watch(trendDays, () => {
  loadEnergyTrend()
})

watch(alertStatDays, () => {
  // 可以重新加载告警统计数据
})

// 初始加载
onMounted(async () => {
  await loadStats()
  initCharts()
  await loadSummaryData()

  window.addEventListener('resize', handleResize)
})

// 组件销毁时清理
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)

  if (energyTrendChart) energyTrendChart.dispose()
  if (buildingRankChart) buildingRankChart.dispose()
  if (alertTypeChart) alertTypeChart.dispose()
  if (alertTrendChart) alertTrendChart.dispose()
  if (alertStatusChart) alertStatusChart.dispose()
})
</script>

<style>
.statistics-container {
  padding: 20px;
}

.statistics-container .stats-row {
  margin-bottom: 20px;
}

.statistics-container .stats-row .stat-card {
  margin-bottom: 20px;
}

.statistics-container .stats-row .stat-card .stat-content {
  display: flex;
  align-items: center;
}

.statistics-container .stats-row .stat-card .stat-content .stat-icon {
  font-size: 36px;
  margin-right: 15px;
}

.statistics-container .stats-row .stat-card .stat-content .stat-info {
  flex: 1;
}

.statistics-container .stats-row .stat-card .stat-content .stat-info .stat-value {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 5px;
  color: #333;
}

.statistics-container .stats-row .stat-card .stat-content .stat-info .stat-title {
  font-size: 14px;
  color: #999;
}

.statistics-container .chart-row {
  margin-bottom: 20px;
}

.statistics-container .chart-row .chart-card {
  margin-bottom: 20px;
}

.statistics-container .chart-row .chart-card .el-card__header {
  padding: 15px 20px;
  border-bottom: 1px solid #e6e6e6;
}

.statistics-container .chart-row .chart-card .el-card__body {
  padding: 20px;
}

.statistics-container .chart-row .chart-card .chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.statistics-container .chart-row .chart-card .chart-header .chart-actions {
  display: flex;
  align-items: center;
}

.statistics-container .chart-row .chart-card .chart {
  width: 100%;
  height: 300px;
}

.statistics-container .chart-row .chart-card .alert-stat-card {
  text-align: center;
}

.statistics-container .chart-row .chart-card .alert-stat-card .stat-title {
  margin-bottom: 15px;
  font-weight: 500;
  color: #333;
}

.statistics-container .chart-row .chart-card .alert-stat-card .small-chart {
  width: 100%;
  height: 200px;
}

.statistics-container .table-row .table-card .el-card__header {
  padding: 15px 20px;
  border-bottom: 1px solid #e6e6e6;
}

.statistics-container .table-row .table-card .el-card__body {
  padding: 0;
}

@media (max-width: 768px) {
  .statistics-container {
    padding: 10px;
  }

  .statistics-container .chart-row .chart-card .chart {
    height: 250px;
  }

  .statistics-container .chart-row .chart-card .alert-stat-card .small-chart {
    height: 150px;
  }
}
</style>
