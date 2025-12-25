<template>
  <div class="power-chart">
    <v-chart
      :option="chartOption"
      :autoresize="true"
      class="chart"
      v-loading="loading"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { api } from '@/api/energyApi'

const props = defineProps({
  meterId: {
    type: [Number, String],
    required: true
  },
  limit: {
    type: Number,
    default: 10
  },
  autoRefresh: {
    type: Boolean,
    default: false
  }
})

const loading = ref(false)
const chartOption = ref({
  title: {
    text: '功率趋势图',
    left: 'center',
    textStyle: {
      fontSize: 14,
      fontWeight: 'normal'
    }
  },
  tooltip: {
    trigger: 'axis',
    formatter: function(params) {
      return `${params[0].axisValue}<br/>功率: ${params[0].value}W`
    }
  },
  xAxis: {
    type: 'category',
    data: [],
    axisLabel: {
      rotate: 45
    }
  },
  yAxis: {
    type: 'value',
    name: '功率(W)',
    axisLabel: {
      formatter: '{value} W'
    }
  },
  series: [
    {
      name: '实时功率',
      type: 'line',
      data: [],
      smooth: true,
      lineStyle: {
        color: '#409eff'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ]
        }
      },
      markLine: {
        silent: true,
        lineStyle: {
          color: '#f56c6c',
          type: 'dashed'
        },
        data: [
          { yAxis: 1000, name: '阈值' }
        ]
      }
    }
  ],
  grid: {
    left: '3%',
    right: '4%',
    bottom: '15%',
    top: '15%',
    containLabel: true
  }
})

let refreshTimer = null

// 加载图表数据
async function loadChartData() {
  try {
    loading.value = true
    const data = await api.energyData.getPowerTrend(props.meterId, props.limit)

    chartOption.value.xAxis.data = data.timestamps
    chartOption.value.series[0].data = data.powerValues

  } catch (error) {
    console.error('加载功率趋势数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 监听属性变化
watch(
  () => props.meterId,
  () => {
    loadChartData()
  },
  { immediate: true }
)

watch(
  () => props.limit,
  () => {
    loadChartData()
  }
)

// 自动刷新
watch(
  () => props.autoRefresh,
  (newVal) => {
    if (newVal) {
      refreshTimer = setInterval(loadChartData, 10000) // 10秒刷新一次
    } else if (refreshTimer) {
      clearInterval(refreshTimer)
      refreshTimer = null
    }
  },
  { immediate: true }
)

// 组件销毁时清理定时器
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style>
.power-chart {
  width: 100%;
  height: 100%;
}

.power-chart .chart {
  width: 100%;
  height: 100%;
  min-height: 300px;
}
</style>
