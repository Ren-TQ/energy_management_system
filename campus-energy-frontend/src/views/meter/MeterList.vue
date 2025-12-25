<template>
  <div class="meter-container">
    <el-card shadow="never" class="main-card">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <el-input
            v-model="searchParams.deviceName"
            placeholder="设备名称"
            style="width: 160px; margin-right: 10px;"
            clearable
          />
          <el-input
            v-model="searchParams.serialNumber"
            placeholder="序列号"
            style="width: 180px; margin-right: 10px;"
            clearable
          />
          <el-select
            v-model="searchParams.buildingId"
            placeholder="所在建筑"
            style="width: 160px; margin-right: 10px;"
            clearable
            filterable
          >
            <el-option
              v-for="building in buildingOptions"
              :key="building.id"
              :label="building.name"
              :value="building.id"
            />
          </el-select>
          <el-select
            v-model="searchParams.status"
            placeholder="设备状态"
            style="width: 120px; margin-right: 10px;"
            clearable
          >
            <el-option label="在线" value="ONLINE" />
            <el-option label="离线" value="OFFLINE" />
          </el-select>
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
            @click="handleAdd"
            v-if="userRole === 'ADMIN'"
          >
            <el-icon><Plus /></el-icon>
            新建设备
          </el-button>
          <el-button @click="refreshData">
            <el-icon><RefreshRight /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="meterList"
        v-loading="loading"
        style="width: 100%"
        :border="true"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="deviceName" label="设备名称" width="180" />
        <el-table-column prop="serialNumber" label="序列号" width="180" />
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
        <el-table-column prop="powerThreshold" label="功率阈值(W)" width="120" align="right">
          <template #default="scope">
            {{ scope.row.powerThreshold }}
          </template>
        </el-table-column>
        <el-table-column prop="buildingName" label="所在建筑" width="150" />
        <el-table-column prop="roomNumber" label="房间号" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="实时数据" width="150">
          <template #default="scope">
            <div class="realtime-data">
              <div v-if="scope.row.latestData">
                <span class="label">功率: </span>
                <span :class="getPowerClass(scope.row.latestData.power, scope.row.powerThreshold)">
                  {{ scope.row.latestData.power }}W
                </span>
              </div>
              <div v-if="scope.row.latestData">
                <span class="label">电压: </span>
                <span>{{ scope.row.latestData.voltage }}V</span>
              </div>
              <div v-else>--</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="handleViewDetail(scope.row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              link
              @click="handleEdit(scope.row)"
              v-if="userRole === 'ADMIN'"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDeactivate(scope.row)"
              v-if="userRole === 'ADMIN' && scope.row.isActive"
            >
              停用
            </el-button>
            <el-button
              type="danger"
              link
              @click="handleDelete(scope.row)"
              v-if="userRole === 'ADMIN'"
            >
              删除
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

    <!-- 设备表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="设备名称" prop="deviceName">
          <el-input
            v-model="formData.deviceName"
            placeholder="请输入设备名称"
          />
        </el-form-item>

        <el-form-item label="序列号" prop="serialNumber">
          <el-input
            v-model="formData.serialNumber"
            placeholder="请输入设备序列号"
            :disabled="!!formData.id"
          />
        </el-form-item>

        <el-form-item label="所在建筑" prop="buildingId">
          <el-select
            v-model="formData.buildingId"
            placeholder="请选择建筑"
            style="width: 100%;"
            filterable
          >
            <el-option
              v-for="building in buildingOptions"
              :key="building.id"
              :label="building.name"
              :value="building.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="房间号" prop="roomNumber">
          <el-input
            v-model="formData.roomNumber"
            placeholder="请输入房间号"
          />
        </el-form-item>

        <el-form-item label="功率阈值(W)" prop="powerThreshold">
          <el-input-number
            v-model="formData.powerThreshold"
            :min="100"
            :max="20000"
            :step="100"
            controls-position="right"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 设备详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="设备详情"
      width="800px"
    >
      <div v-if="currentMeter" class="meter-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备ID">{{ currentMeter.id }}</el-descriptions-item>
          <el-descriptions-item label="设备名称">{{ currentMeter.deviceName }}</el-descriptions-item>
          <el-descriptions-item label="序列号">{{ currentMeter.serialNumber }}</el-descriptions-item>
          <el-descriptions-item label="设备状态">
            <el-tag :type="currentMeter.status === 'ONLINE' ? 'success' : 'danger'">
              {{ currentMeter.status === 'ONLINE' ? '在线' : '离线' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="功率阈值">{{ currentMeter.powerThreshold }}W</el-descriptions-item>
          <el-descriptions-item label="所在建筑">{{ currentMeter.buildingName }}</el-descriptions-item>
          <el-descriptions-item label="房间号">{{ currentMeter.roomNumber }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDateTime(currentMeter.createdAt) }}</el-descriptions-item>
        </el-descriptions>

        <div class="realtime-section" v-if="currentMeter.latestData">
          <h3>实时数据</h3>
          <el-row :gutter="20">
            <el-col :span="6">
              <el-statistic title="电压" :value="currentMeter.latestData.voltage" suffix="V" />
            </el-col>
            <el-col :span="6">
              <el-statistic title="电流" :value="currentMeter.latestData.current" suffix="A" />
            </el-col>
            <el-col :span="6">
              <el-statistic
                title="功率"
                :value="currentMeter.latestData.power"
                suffix="W"
                :value-style="getPowerStyle(currentMeter.latestData.power, currentMeter.powerThreshold)"
              />
            </el-col>
            <el-col :span="6">
              <el-statistic
                title="累计用电"
                :value="currentMeter.latestData.energyConsumption"
                suffix="kWh"
              />
            </el-col>
          </el-row>
        </div>

        <div class="chart-section">
          <h3>功率趋势</h3>
          <PowerChart
            :meter-id="currentMeter.id"
            :limit="10"
            :auto-refresh="true"
            style="height: 300px;"
          />
        </div>
      </div>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, RefreshRight } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'
import { formatDateTime } from '@/utils/date'
import PowerChart from '@/components/charts/PowerChart.vue'

const store = useStore()

// 用户角色 - 强制返回ADMIN用于测试
const userRole = computed(() => 'ADMIN')

// 数据
const meterList = ref([])
const buildingOptions = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索参数
const searchParams = reactive({
  deviceName: '',
  serialNumber: '',
  buildingId: '',
  status: ''
})

// 表单相关
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const submitting = ref(false)
const currentMeter = ref(null)

const formData = reactive({
  id: null,
  deviceName: '',
  serialNumber: '',
  buildingId: '',
  roomNumber: '',
  powerThreshold: 1000
})

// 表单验证规则
const formRules = {
  deviceName: [
    { required: true, message: '请输入设备名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  serialNumber: [
    { required: true, message: '请输入设备序列号', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' },
    { pattern: /^[A-Z0-9_]+$/, message: '只能包含大写字母、数字和下划线', trigger: 'blur' }
  ],
  buildingId: [
    { required: true, message: '请选择所在建筑', trigger: 'change' }
  ],
  roomNumber: [
    { required: true, message: '请输入房间号', trigger: 'blur' }
  ],
  powerThreshold: [
    { required: true, message: '请输入功率阈值', trigger: 'blur' },
    { type: 'number', min: 100, max: 20000, message: '功率阈值在 100 到 20000 之间', trigger: 'blur' }
  ]
}

// 获取功率显示类名
function getPowerClass(power, threshold) {
  if (!power || !threshold) return ''
  return power > threshold ? 'power-over' : 'power-normal'
}

// 获取功率样式
function getPowerStyle(power, threshold) {
  if (!power || !threshold) return {}
  return power > threshold ? { color: '#f56c6c' } : { color: '#67c23a' }
}

// 加载建筑选项
async function loadBuildingOptions() {
  try {
    const buildings = await api.building.getAll()
    buildingOptions.value = buildings
  } catch (error) {
    console.error('加载建筑选项失败:', error)
  }
}

// 加载设备列表
async function loadMeters() {
  try {
    loading.value = true

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...searchParams
    }

    const response = await api.meter.getAll(params)
    meterList.value = response.content
    total.value = response.totalElements

    // 为每个设备加载最新数据
    for (const meter of meterList.value) {
      try {
        const latestData = await api.energyData.getLatest(meter.id)
        meter.latestData = latestData
      } catch (error) {
        console.error(`加载设备${meter.serialNumber}数据失败:`, error)
      }
    }

  } catch (error) {
    console.error('加载设备列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadMeters()
}

// 重置搜索
function resetSearch() {
  searchParams.deviceName = ''
  searchParams.serialNumber = ''
  searchParams.buildingId = ''
  searchParams.status = ''
  currentPage.value = 1
  loadMeters()
}

// 刷新数据
function refreshData() {
  loadMeters()
}

// 分页大小变化
function handleSizeChange(size) {
  pageSize.value = size
  loadMeters()
}

// 页码变化
function handlePageChange(page) {
  currentPage.value = page
  loadMeters()
}

// 查看详情
function handleViewDetail(row) {
  currentMeter.value = row
  detailDialogVisible.value = true
}

// 添加设备
function handleAdd() {
  dialogTitle.value = '新建设备'
  resetForm()
  dialogVisible.value = true
}

// 编辑设备
function handleEdit(row) {
  dialogTitle.value = '编辑设备'

  // 填充表单数据
  formData.id = row.id
  formData.deviceName = row.deviceName
  formData.serialNumber = row.serialNumber
  formData.buildingId = row.buildingId
  formData.roomNumber = row.roomNumber
  formData.powerThreshold = row.powerThreshold

  dialogVisible.value = true
}

// 停用设备
async function handleDeactivate(row) {
  try {
    await ElMessageBox.confirm(
      `确定要停用设备 "${row.deviceName}" 吗？`,
      '警告',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    await api.meter.deactivate(row.id)
    ElMessage.success('停用成功')
    loadMeters()

  } catch (error) {
    if (error !== 'cancel') {
      console.error('停用设备失败:', error)
      ElMessage.error(error.message || '停用失败')
    }
  }
}

// 删除设备
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除设备 "${row.deviceName}" 吗？此操作不可恢复。`,
      '警告',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    await api.meter.delete(row.id)
    ElMessage.success('删除成功')
    loadMeters()

  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除设备失败:', error)
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 重置表单
function resetForm() {
  if (formRef.value) {
    formRef.value.resetFields()
  }

  formData.id = null
  formData.deviceName = ''
  formData.serialNumber = ''
  formData.buildingId = ''
  formData.roomNumber = ''
  formData.powerThreshold = 1000
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (formData.id) {
      // 编辑
      await api.meter.update(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      // 新建
      await api.meter.create(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    loadMeters()

  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 初始加载
onMounted(async () => {
  await loadBuildingOptions()
  await loadMeters()
})
</script>

<style>
.meter-container {
  padding: 20px;
}

.meter-container .main-card .el-card__body {
  padding: 20px;
}

.meter-container .action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.meter-container .action-bar .search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.meter-container .action-bar .action-buttons {
  display: flex;
  align-items: center;
  gap: 10px;
}

.meter-container .realtime-data {
  font-size: 12px;
}

.meter-container .realtime-data .label {
  color: #999;
}

.meter-container .realtime-data .power-over {
  color: #f56c6c;
  font-weight: bold;
}

.meter-container .realtime-data .power-normal {
  color: #67c23a;
}

.meter-container .pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.meter-container .meter-detail .realtime-section {
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 6px;
}

.meter-container .meter-detail .realtime-section h3 {
  margin: 0 0 20px;
  color: #333;
}

.meter-container .meter-detail .chart-section {
  margin-top: 20px;
}

.meter-container .meter-detail .chart-section h3 {
  margin: 0 0 10px;
  color: #333;
}

.meter-container .dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .meter-container {
    padding: 10px;
  }

  .meter-container .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .meter-container .action-bar .search-bar,
  .meter-container .action-bar .action-buttons {
    justify-content: center;
    width: 100%;
  }

  .meter-container .action-bar .search-bar {
    margin-bottom: 10px;
  }

  .meter-container .meter-detail .realtime-section .el-row .el-col {
    margin-bottom: 10px;
  }
}
</style>
