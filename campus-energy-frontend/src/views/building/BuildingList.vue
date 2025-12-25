<template>
  <div class="building-container">
    <el-card shadow="never" class="main-card">
      <!-- 操作栏 -->
      <div class="action-bar">
        <div class="search-bar">
          <el-input
            v-model="searchParams.name"
            placeholder="搜索建筑名称"
            style="width: 200px; margin-right: 10px;"
            clearable
          />
          <el-select
            v-model="searchParams.type"
            placeholder="建筑类型"
            style="width: 120px; margin-right: 10px;"
            clearable
          >
            <el-option
              v-for="type in buildingTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
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
            新建建筑
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        :data="buildingList"
        v-loading="loading"
        style="width: 100%"
        :border="true"
        stripe
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="name" label="建筑名称" width="180" />
        <el-table-column prop="type" label="建筑类型" width="120">
          <template #default="scope">
            <el-tag :type="getBuildingTypeTag(scope.row.type)" size="small">
              {{ getBuildingTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="locationCode" label="位置编号" width="120" />
        <el-table-column prop="floors" label="楼层数" width="100" align="center" />
        <el-table-column prop="meterCount" label="设备数量" width="100" align="center" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button
              type="primary"
              link
              @click="handleView(scope.row)"
            >
              查看
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

    <!-- 建筑表单对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="建筑名称" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入建筑名称"
          />
        </el-form-item>

        <el-form-item label="建筑类型" prop="type">
          <el-select
            v-model="formData.type"
            placeholder="请选择建筑类型"
            style="width: 100%;"
          >
            <el-option
              v-for="type in buildingTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="位置编号" prop="locationCode">
          <el-input
            v-model="formData.locationCode"
            placeholder="请输入位置编号"
          />
        </el-form-item>

        <el-form-item label="楼层数" prop="floors">
          <el-input-number
            v-model="formData.floors"
            :min="1"
            :max="50"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { api } from '@/api/energyApi'
import { formatDateTime } from '@/utils/date'

const store = useStore()

// 用户角色 - 强制返回ADMIN用于测试
const userRole = computed(() => 'ADMIN')

// 数据
const buildingList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索参数
const searchParams = reactive({
  name: '',
  type: ''
})

// 表单相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref()
const submitting = ref(false)
const formData = reactive({
  id: null,
  name: '',
  type: '',
  locationCode: '',
  floors: 1
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入建筑名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择建筑类型', trigger: 'change' }
  ],
  floors: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, max: 50, message: '楼层数在 1 到 50 之间', trigger: 'blur' }
  ]
}

// 建筑类型选项
const buildingTypes = [
  { value: 'DORMITORY', label: '宿舍' },
  { value: 'CLASSROOM', label: '教学楼' },
  { value: 'LABORATORY', label: '实验室' },
  { value: 'LIBRARY', label: '图书馆' },
  { value: 'OFFICE', label: '办公楼' }
]

// 获取建筑类型标签
function getBuildingTypeTag(type) {
  const typeMap = {
    'DORMITORY': 'success',
    'CLASSROOM': 'primary',
    'LABORATORY': 'warning',
    'LIBRARY': 'info',
    'OFFICE': ''
  }
  return typeMap[type] || 'default'
}

// 获取建筑类型文本
function getBuildingTypeText(type) {
  const typeObj = buildingTypes.find(t => t.value === type)
  return typeObj ? typeObj.label : type
}

// 加载建筑列表
async function loadBuildings() {
  try {
    loading.value = true

    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      ...searchParams
    }

    const response = await api.building.getList(params)
    buildingList.value = response.content
    total.value = response.totalElements

  } catch (error) {
    console.error('加载建筑列表失败:', error)
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 搜索
function handleSearch() {
  currentPage.value = 1
  loadBuildings()
}

// 重置搜索
function resetSearch() {
  searchParams.name = ''
  searchParams.type = ''
  currentPage.value = 1
  loadBuildings()
}

// 分页大小变化
function handleSizeChange(size) {
  pageSize.value = size
  loadBuildings()
}

// 页码变化
function handlePageChange(page) {
  currentPage.value = page
  loadBuildings()
}

// 查看详情
function handleView(row) {
  ElMessage.info(`查看建筑: ${row.name}`)
}

// 添加建筑
function handleAdd() {
  dialogTitle.value = '新建建筑'
  resetForm()
  dialogVisible.value = true
}

// 编辑建筑
function handleEdit(row) {
  dialogTitle.value = '编辑建筑'

  // 填充表单数据
  formData.id = row.id
  formData.name = row.name
  formData.type = row.type
  formData.locationCode = row.locationCode
  formData.floors = row.floors

  dialogVisible.value = true
}

// 删除建筑
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(
      `确定要删除建筑 "${row.name}" 吗？此操作不可恢复。`,
      '警告',
      {
        type: 'warning',
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      }
    )

    await api.building.delete(row.id)
    ElMessage.success('删除成功')
    loadBuildings()

  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除建筑失败:', error)
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
  formData.name = ''
  formData.type = ''
  formData.locationCode = ''
  formData.floors = 1
}

// 提交表单
async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    submitting.value = true

    if (formData.id) {
      // 编辑
      await api.building.update(formData.id, formData)
      ElMessage.success('更新成功')
    } else {
      // 新建
      await api.building.create(formData)
      ElMessage.success('创建成功')
    }

    dialogVisible.value = false
    loadBuildings()

  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

// 初始加载
onMounted(() => {
  loadBuildings()
})
</script>

<style>
.building-container {
  padding: 20px;
}

.building-container .main-card .el-card__body {
  padding: 20px;
}

.building-container .action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.building-container .action-bar .search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.building-container .action-bar .action-buttons {
  display: flex;
  align-items: center;
}

.building-container .pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.building-container .dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 768px) {
  .building-container {
    padding: 10px;
  }

  .building-container .action-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .building-container .action-bar .search-bar,
  .building-container .action-bar .action-buttons {
    justify-content: center;
    width: 100%;
  }

  .building-container .action-bar .search-bar {
    margin-bottom: 10px;
  }
}
</style>
