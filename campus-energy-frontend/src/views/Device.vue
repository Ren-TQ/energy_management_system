<!-- ============================================ -->
<!-- MVVM架构 - View层 (视图层)                  -->
<!-- 职责：负责UI展示和用户交互界面               -->
<!-- ============================================ -->
<template>
  <div class="device-page">
    <div class="card-box">
      <div class="page-header">
        <h2 class="page-title">设备管理</h2>
        <el-button v-if="userStore.isAdmin" type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>
          新增设备
        </el-button>
      </div>
      
      <!-- 筛选 -->
      <div class="search-bar">
        <el-form :inline="true">
          <el-form-item label="所属建筑">
            <el-select v-model="filterBuilding" placeholder="全部" clearable @change="loadData">
              <el-option
                v-for="item in buildings"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filterStatus" placeholder="全部" clearable @change="loadData">
              <el-option label="在线" value="ONLINE" />
              <el-option label="离线" value="OFFLINE" />
              <el-option label="维护中" value="MAINTENANCE" />
              <el-option label="已停用" value="DECOMMISSIONED" />
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 数据表格 -->
      <el-table :data="filteredData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="设备名称" width="140" />
        <el-table-column prop="serialNumber" label="序列号(SN)" width="170">
          <template #default="{ row }">
            <code>{{ row.serialNumber }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="statusLabel" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ratedPower" label="额定功率(W)" width="120" align="center" />
        <el-table-column prop="buildingName" label="所属建筑" width="130" />
        <el-table-column prop="roomNumber" label="房间号" width="120" />
        <el-table-column prop="usageDescription" label="用途" show-overflow-tooltip />
        <el-table-column v-if="userStore.isAdmin" label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-dropdown @command="(cmd) => handleStatusChange(row.id, cmd)">
              <el-button type="warning" link>状态</el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="ONLINE">设为在线</el-dropdown-item>
                  <el-dropdown-item command="OFFLINE">设为离线</el-dropdown-item>
                  <el-dropdown-item command="MAINTENANCE">设为维护中</el-dropdown-item>
                  <el-dropdown-item command="DECOMMISSIONED">设为已停用</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-popconfirm
              title="确定要删除此设备吗？"
              @confirm="handleDelete(row.id)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑设备' : '新增设备'"
      width="550px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="序列号(SN)" prop="serialNumber">
          <el-input v-model="form.serialNumber" placeholder="如: METER_DORM_301" />
        </el-form-item>
        <el-form-item label="所属建筑" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择建筑" style="width: 100%">
            <el-option
              v-for="item in buildings"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="如: 301、一楼大厅" />
        </el-form-item>
        <el-form-item label="额定功率(W)" prop="ratedPower">
          <el-input-number v-model="form.ratedPower" :min="100" :max="50000" :step="100" />
        </el-form-item>
        <el-form-item label="用途描述" prop="usageDescription">
          <el-input v-model="form.usageDescription" placeholder="如: 学生宿舍（限电1000W）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
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
// 导入状态管理（Pinia Store）
import { useUserStore } from '@/stores/user'
// ============================================
// MVVM架构 - Model层 (数据模型层)
// 职责：与后端API通信，获取和提交数据
// 位置：@/api/device.js 和 @/api/building.js
// ============================================
import { getDevices, createDevice, updateDevice, updateDeviceStatus, deleteDevice } from '@/api/device'
import { getBuildings } from '@/api/building'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const buildings = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const filterBuilding = ref('')
const filterStatus = ref('')

const form = reactive({
  name: '',
  serialNumber: '',
  buildingId: '',
  roomNumber: '',
  ratedPower: 1000,
  usageDescription: ''
})

const rules = {
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  serialNumber: [{ required: true, message: '请输入序列号', trigger: 'blur' }],
  buildingId: [{ required: true, message: '请选择所属建筑', trigger: 'change' }],
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  ratedPower: [{ required: true, message: '请输入额定功率', trigger: 'blur' }]
}

// 过滤后的数据
const filteredData = computed(() => {
  let data = tableData.value
  if (filterBuilding.value) {
    data = data.filter(item => item.buildingId === filterBuilding.value)
  }
  if (filterStatus.value) {
    data = data.filter(item => item.status === filterStatus.value)
  }
  return data
})

// 加载建筑列表
async function loadBuildings() {
  try {
    const res = await getBuildings()
    buildings.value = res.data || []
  } catch (error) {
    console.error('加载建筑列表失败:', error)
  }
}

// 加载设备数据
async function loadData() {
  loading.value = true
  try {
    const res = await getDevices()
    tableData.value = res.data || []
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 状态标签类型
function getStatusType(status) {
  const map = {
    'ONLINE': 'success',
    'OFFLINE': 'danger',
    'MAINTENANCE': 'warning',
    'DECOMMISSIONED': 'info'
  }
  return map[status] || 'info'
}

// 打开对话框
function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, {
      name: row.name,
      serialNumber: row.serialNumber,
      buildingId: row.buildingId,
      roomNumber: row.roomNumber,
      ratedPower: row.ratedPower,
      usageDescription: row.usageDescription
    })
  } else {
    isEdit.value = false
    editId.value = null
  }
  dialogVisible.value = true
}

// 重置表单
function resetForm() {
  Object.assign(form, {
    name: '',
    serialNumber: '',
    buildingId: '',
    roomNumber: '',
    ratedPower: 1000,
    usageDescription: ''
  })
  formRef.value?.resetFields()
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      if (isEdit.value) {
        await updateDevice(editId.value, form)
        ElMessage.success('更新成功')
      } else {
        await createDevice(form)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (error) {
      console.error('操作失败:', error)
    } finally {
      submitting.value = false
    }
  })
}

// 修改状态
async function handleStatusChange(id, status) {
  try {
    await updateDeviceStatus(id, status)
    ElMessage.success('状态更新成功')
    loadData()
  } catch (error) {
    console.error('状态更新失败:', error)
  }
}

// 删除
async function handleDelete(id) {
  try {
    await deleteDevice(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

onMounted(() => {
  loadBuildings()
  loadData()
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
}

code {
  background-color: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
}
</style>

