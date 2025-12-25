<template>
  <div class="energy-chart">
    <div class="chart-header">
      <span class="title">今日能耗分布</span>
      <el-button
        v-if="showRefresh"
        type="text"
        size="small"
        @click="loadChartData"
      >
        刷新
      </el-button>
    </div>
    <v-chart
      :option="chartOption"
      :autoresize="true"
      class="chart"
      v-loading="loading"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { api } from '@/api/energyApi'

const props = defineProps({
  showRefresh: {
    type: Boolean,
    default: true
  }
})

const loading = ref(false)
const chartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: function(params) {
      return `${params.name}<br/>能耗: ${params.value} kWh<br/>占比: ${params.percent}%`
    }
  },
  legend: {
    type: 'scroll',
    orient: 'vertical',
    right: 10,
    top: 'center',
    textStyle: {
      fontSize: 12
    }
  },
  series: [
    {
      name: '能耗分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      },
      label: {
        show: false
      },
      labelLine: {
        show: false
      }
    }
  ],
  color: [
    '#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399',
    '#a0d911', '#fa8c16', '#722ed1', '#13c2c2', '#eb2f96'
  ]
})

// 加载图表数据
async function loadChartData() {
  try {
    loading.value = true
    const data = await api.energyData.getEnergyDistribution()

    // 处理数据
    const chartData = data.labels.map((label, index) => ({
      name: label,
      value: data.values[index]
    }))

    chartOption.value.series[0].data = chartData

  } catch (error) {
    console.error('加载能耗分布数据失败:', error)
    // 显示空状态
    chartOption.value.series[0].data = []
  } finally {
    loading.value = false
  }
}

// 初始加载
loadChartData()
</script>

<style>
.energy-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.energy-chart .chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  margin-bottom: 10px;
}

.energy-chart .chart-header .title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.energy-chart .chart {
  flex: 1;
  min-height: 300px;
}
</style>
