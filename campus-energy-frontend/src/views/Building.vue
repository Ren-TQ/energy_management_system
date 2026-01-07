<template>
  <div class="building-page">
    <div class="card-box">
      <div class="page-header">
        <h2 class="page-title">建筑管理</h2>
        <el-button v-if="userStore.isAdmin" type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon>
          新增建筑
        </el-button>
      </div>
      
      <!-- 数据表格 -->
      <el-table :data="tableData" v-loading="loading" stripe border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="建筑名称" width="150" />
        <el-table-column prop="locationCode" label="位置编号" width="150" />
        <el-table-column prop="floorCount" label="楼层数" width="100" align="center" />
        <el-table-column prop="category" label="用途分类" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="deviceCount" label="设备数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="success">{{ row.deviceCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column v-if="userStore.isAdmin" label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-popconfirm
              title="确定要删除此建筑吗？"
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
      :title="isEdit ? '编辑建筑' : '新增建筑'"
      width="500px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="建筑名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入建筑名称" />
        </el-form-item>
        <el-form-item label="位置编号" prop="locationCode">
          <el-input v-model="form.locationCode" placeholder="如: BLD_001" />
        </el-form-item>
        <el-form-item label="楼层数" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="用途分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择" allow-create filterable>
            <el-option label="教学楼" value="教学楼" />
            <el-option label="宿舍楼" value="宿舍楼" />
            <el-option label="图书馆" value="图书馆" />
            <el-option label="办公楼" value="办公楼" />
            <el-option label="食堂" value="食堂" />
            <el-option label="实验楼" value="实验楼" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入建筑描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { useUserStore } from '@/stores/user'
import { getBuildings, createBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const editId = ref(null)

const form = reactive({
  name: '',
  locationCode: '',
  floorCount: 1,
  category: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入建筑名称', trigger: 'blur' }],
  locationCode: [{ required: true, message: '请输入位置编号', trigger: 'blur' }],
  floorCount: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  category: [{ required: true, message: '请选择用途分类', trigger: 'change' }]
}

// 加载数据
async function loadData() {
  loading.value = true
  try {
    const res = await getBuildings()
    tableData.value = res.data || []
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 打开对话框
function openDialog(row) {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, {
      name: row.name,
      locationCode: row.locationCode,
      floorCount: row.floorCount,
      category: row.category,
      description: row.description
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
    locationCode: '',
    floorCount: 1,
    category: '',
    description: ''
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
        await updateBuilding(editId.value, form)
        ElMessage.success('更新成功')
      } else {
        await createBuilding(form)
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

// 删除
async function handleDelete(id) {
  try {
    await deleteBuilding(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

// 格式化时间
function formatTime(time) {
  if (!time) return '-'
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

onMounted(() => {
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
</style>

