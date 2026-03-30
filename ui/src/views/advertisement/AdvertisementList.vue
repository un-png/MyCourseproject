<template>
  <div class="advertisement">
    <div class="page-header">
      <h2>广告管理</h2>
      <el-button type="primary" @click="$router.push('/advertisement/edit')">
        新建广告
      </el-button>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="广告标题">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入广告标题"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="广告位">
          <el-select
            v-model="queryParams.position"
            placeholder="请选择广告位"
            clearable
          >
            <el-option
              v-for="item in positions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="广告类型">
          <el-select
            v-model="queryParams.type"
            placeholder="请选择广告类型"
            clearable
          >
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="请选择状态"
            clearable
          >
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="active" />
            <el-option label="已结束" value="ended" />
            <el-option label="已下架" value="offline" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="title" label="广告标题" min-width="100" />
        <el-table-column prop="position" label="广告位" width="100">
          <template #default="{ row }">
            {{ row.position }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="广告类型" width="80">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="广告图片" width="120">
          <template #default="{ row }">
            <el-image
              v-if="row.mediaUrl"
              :src="row.mediaUrl"
              :preview-src-list="[row.mediaUrl]"
              fit="cover"
              style="width: 80px; height: 40px"
            />
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="clickCount" label="访问量" width="100">
          <template #default="{ row }">
            {{ row.clickCount || 0 }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                v-if="row.status === '待投放'"
                type="success"
                link
                @click="handlePublish(row)"
              >
                发布
              </el-button>
              <el-button
                v-if="row.status === '进行中'"
                type="warning"
                link
                @click="handleOffline(row)"
              >
                下架
              </el-button>
              <el-button
                type="danger"
                link
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑广告" width="50%">
      <el-form :model="editForm" label-width="120px">
        <el-form-item label="广告标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="广告位">
          <el-select v-model="editForm.position" placeholder="请选择广告位">
            <el-option
              v-for="item in positions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="广告类型">
          <el-select v-model="editForm.type" placeholder="请选择广告类型">
            <el-option label="图片" value="image" />
            <el-option label="视频" value="video" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" placeholder="请选择状态">
            <el-option label="待投放" value="待投放" />
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
            <el-option label="已下架" value="已下架" />
          </el-select>
        </el-form-item>
        <el-form-item label="媒体URL">
          <el-input v-model="editForm.mediaUrl" />
        </el-form-item>
        <el-form-item label="链接">
          <el-input v-model="editForm.link" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="editForm.startTime" type="datetime" placeholder="选择开始时间" />
        </el-form-item>
        <el-form-item label="结束时间">
          <el-date-picker v-model="editForm.endTime" type="datetime" placeholder="选择结束时间" />
        </el-form-item>
        <el-form-item label="目标用户">
          <el-select v-model="editForm.targetUser" placeholder="请选择目标用户">
            <el-option label="全部用户" value="全部用户" />
            <el-option label="新用户" value="新用户" />
            <el-option label="老用户" value="老用户" />
            <el-option label="VIP用户" value="VIP用户" />

          </el-select>
        </el-form-item>
        <el-form-item label="点击量">
          <el-input v-model="editForm.clickCount" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveEdit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])
const dialogVisible = ref(false)
const editForm = ref({
  advertisementId: '',
  title: '',
  position: '',
  type: '',
  status: '',
  mediaUrl: '',
  link: '',
  startTime: null,
  endTime: null,
  targetUser: '',
  clickCount: 0
})

const positions = [
  { label: '首页轮播', value: '首页轮播' },
  { label: '首页侧边', value: '首页侧边' },
  { label: '文章页侧边', value: '文章页侧边' },
  { label: '文章页底部', value: '文章页底部' }
]

const queryParams = ref({
  title: '',
  position: '',
  type: '',
  status: ''
})

const getPositionText = (position) => {
  const found = positions.find(item => item.value === position)
  return found ? found.label : position
}

const getTypeTag = (type) => {
  const map = {
    image: 'success',
    video: 'warning'
  }
  return map[type] || 'info'
}

const getTypeText = (type) => {
  const map = {
    image: '图片',
    video: '视频'
  }
  return map[type] || type
}

const getStatusType = (status) => {
  const map = {
    待投放: 'info',
    进行中: 'success',
    已结束: 'warning',
    已下架: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    待投放: '待投放',
    进行中: '进行中',
    已结束: '已结束',
    已下架: '已下架'
  }
  return map[status] || status
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  // 如果是数组，直接用参数构造Date对象
  if (Array.isArray(dateTime)) {
    // 补全缺失的秒数
    const [year, month, day, hour = 0, minute = 0, second = 0] = dateTime
    const date = new Date(year, month - 1, day, hour, minute, second)
    if (isNaN(date.getTime())) return '无效日期'
    return date.toLocaleString()
  }
  // 下面是原有的字符串处理逻辑
  const str = String(dateTime).trim()
  const match = str.match(/^(\d{4})-(\d{2})-(\d{2})[ T](\d{2}):(\d{2}):(\d{2})/)
  if (match) {
    const [ , year, month, day, hour, minute, second ] = match
    const date = new Date(
      Number(year),
      Number(month) - 1,
      Number(day),
      Number(hour),
      Number(minute),
      Number(second)
    )
    if (isNaN(date.getTime())) return '无效日期'
    return date.toLocaleString()
  }
  return '无效日期'
}

const handleQuery = async () => {
  await fetchData()
}

const resetQuery = () => {
  Object.keys(queryParams.value).forEach(key => {
    queryParams.value[key] = ''
  })
  handleQuery()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

const arrToDate = (arr) => {
  if (!arr || !Array.isArray(arr)) return null
  const [year, month, day, hour = 0, minute = 0, second = 0] = arr
  return new Date(year, month - 1, day, hour, minute, second)
}

const checkTimeAndUpdateStatus = () => {
  const now = new Date()
  tableData.value.forEach(row => {
    const startTime = arrToDate(row.startTime)
    const endTime = arrToDate(row.endTime)
    if (startTime && now >= startTime && row.status === '待投放') {
      handlePublish(row, true)
      console.log('自动发布广告:', row.title)
    } else if (endTime && now >= endTime && row.status === '进行中') {
      handleOffline(row, true)
      console.log('自动下架广告:', row.title)
    }
  })
}

const handlePublish = async (row, auto = false) => {
  try {
    if (!auto) {
      await ElMessageBox.confirm('确定要发布该广告吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    }
    const dataToSave = { ...row, status: '进行中' }
    const response = await axios.post('/advertisement/advertisementEdit', dataToSave)
    if (response.data.code === 200) {
      if (!auto) ElMessage.success('发布成功')
      fetchData()
    } else {
      if (!auto) ElMessage.error(response.data.msg || '发布失败')
    }
  } catch (error) {
    if (!auto && error !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const handleOffline = async (row, auto = false) => {
  try {
    if (!auto) {
      await ElMessageBox.confirm('确定要下架该广告吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
    }
    const dataToSave = { ...row, status: '已下架' }
    const response = await axios.post('/advertisement/advertisementEdit', dataToSave)
    if (response.data.code === 200) {
      if (!auto) ElMessage.success('下架成功')
      fetchData()
    } else {
      if (!auto) ElMessage.error(response.data.msg || '下架失败')
    }
  } catch (error) {
    if (!auto && error !== 'cancel') {
      ElMessage.error('下架失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该广告吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete('/advertisement/remove?advertisementId='+row.advertisementId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleEdit = (row) => {
  const copy = JSON.parse(JSON.stringify(row))
  copy.startTime = arrToDate(copy.startTime)
  copy.endTime = arrToDate(copy.endTime)
  editForm.value = copy
  dialogVisible.value = true
}

const saveEdit = async () => {
  // 处理日期格式
  const formatDateForBackend = (date) => {
    if (!date) return null
    return [
      date.getFullYear(),
      date.getMonth() + 1,
      date.getDate(),
      date.getHours(),
      date.getMinutes(),
      date.getSeconds()
    ]
  }

  const dataToSave = {
    ...editForm.value,
    startTime: formatDateForBackend(editForm.value.startTime),
    endTime: formatDateForBackend(editForm.value.endTime)
  }

  try {
    // advertisementId 一定要在 dataToSave 里
    const response = await axios.post('/advertisement/advertisementEdit', dataToSave)
    if (response.data.code === 200) {
      ElMessage.success('编辑成功')
      dialogVisible.value = false
      fetchData()
    } else {
      ElMessage.error(response.data.msg || '编辑失败')
    }
  } catch (error) {
    ElMessage.error('编辑失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await axios.get('/advertisement/advertisementList', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        title: queryParams.value.title || undefined,
        position: queryParams.value.position || undefined,
        type: queryParams.value.type || undefined,
        status: queryParams.value.status || undefined
      }
    })
    console.log('广告原始数据:', response.data.data.items)
    if (response.data.code === 200) {
      tableData.value = response.data.data.items
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('获取广告列表失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 定时检查时间
setInterval(checkTimeAndUpdateStatus, 1000) // 每分钟检查一次

// 初始加载
fetchData()
</script>

<style scoped>
.advertisement {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 