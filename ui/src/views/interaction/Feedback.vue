<template>
  <div class="feedback">
    <div class="page-header">
      <h2>反馈管理</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="反馈类型">
          <el-select v-model="filterForm.feedbackType" placeholder="请选择类型" clearable>
            <el-option label="功能建议" value="功能建议" />
            <el-option label="问题反馈" value="问题反馈" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="filterForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.state" placeholder="请选择状态" clearable>
            <el-option label="待处理" value="待处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已关闭" value="已关闭" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
      >
        <el-table-column prop="feedbackType" label="反馈类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.feedbackType)">
              {{ getTypeText(row.feedbackType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="100" />
        <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="user" label="提交用户" width="100" />
        <el-table-column prop="submitTime" label="提交时间" width="170" />
        <el-table-column prop="state" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.state)">
              {{ getStatusText(row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleView(row)"
              >
                查看
              </el-button>
              <el-button
                v-if="row.state === '待处理'"
                type="success"
                link
                @click="handleProcess(row)"
              >
                处理
              </el-button>
              <el-button
                v-if="row.state === '处理中'"
                type="success"
                link
                @click="handleComplete(row)"
              >
                完成
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

    <!-- 查看对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="反馈详情"
      width="600px"
    >
      <div v-if="currentFeedback" class="feedback-detail">
        <div class="detail-item">
          <span class="label">反馈类型：</span>
          <el-tag :type="getTypeTag(currentFeedback.feedbackType)">
            {{ getTypeText(currentFeedback.feedbackType) }}
          </el-tag>
        </div>
        <div class="detail-item">
          <span class="label">标题：</span>
          <span>{{ currentFeedback.title }}</span>
        </div>
        <div class="detail-item">
          <span class="label">内容：</span>
          <div class="content">{{ currentFeedback.content }}</div>
        </div>
        <div class="detail-item">
          <span class="label">提交用户：</span>
          <span>{{ currentFeedback.user }}</span>
        </div>
        <div class="detail-item">
          <span class="label">提交时间：</span>
          <span>{{ currentFeedback.submitTime }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(currentFeedback.state)">
            {{ getStatusText(currentFeedback.state) }}
          </el-tag>
        </div>
        <div v-if="currentFeedback.reply" class="detail-item">
          <span class="label">回复：</span>
          <div class="content">{{ currentFeedback.reply }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
          <el-button
            v-if="currentFeedback?.state === '待处理'"
            type="primary"
            @click="handleProcess(currentFeedback)"
          >
            处理
          </el-button>
          <el-button
            v-if="currentFeedback?.state === '处理中'"
            type="success"
            @click="handleComplete(currentFeedback)"
          >
            完成
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 处理对话框 -->
    <el-dialog
      v-model="processDialogVisible"
      title="处理反馈"
      width="500px"
    >
      <el-form
        ref="processFormRef"
        :model="processForm"
        :rules="processRules"
        label-width="80px"
      >
        <el-form-item label="处理状态" prop="state">
          <el-radio-group v-model="processForm.state">
            <el-radio label="处理中">处理中</el-radio>
            <el-radio label="已完成">已完成</el-radio>
            <el-radio label="已关闭">已关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="回复内容" prop="reply">
          <el-input
            v-model="processForm.reply"
            type="textarea"
            rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="processDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitProcess" :loading="processLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const tableData = ref([])

const filterForm = reactive({
  feedbackType: '',
  state: '',
  title: ''
})  

const getTypeTag = (type) => {
  const map = {
    '功能建议': 'success',
    '问题反馈': 'danger',
    '其他': 'info'
  }
  return map[type] || 'info'
}

const getTypeText = (type) => {
  const map = {
    '功能建议': '功能建议',
    '问题反馈': '问题反馈',
    '其他': '其他'
  }
  return map[type] || type
}

const getStatusType = (status) => {
  const map = {
    '待处理': 'warning',
    '处理中': 'primary',
    '已完成': 'success',
    '已关闭': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    '待处理': '待处理',
    '处理中': '处理中',
    '已完成': '已完成',
    '已关闭': '已关闭'
  }
  return map[status] || status
}

const handleSearch = async () => {
  await fetchData()
}

const resetFilter = () => {
  Object.keys(filterForm).forEach(key => {
    filterForm[key] = ''
  })
  handleSearch()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  fetchData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchData()
}

// 查看相关
const viewDialogVisible = ref(false)
const currentFeedback = ref(null)

const handleView = (row) => {
  currentFeedback.value = row
  viewDialogVisible.value = true
}

// 处理相关
const processDialogVisible = ref(false)
const processLoading = ref(false)
const processFormRef = ref(null)

const processForm = reactive({
  state: 'processing',
  reply: ''
})

const processRules = {
  state: [{ required: true, message: '请选择处理状态', trigger: 'change' }],
  reply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const handleProcess = (row) => {
  currentFeedback.value = row
  processForm.state = '处理中'
  processForm.reply = ''
  processDialogVisible.value = true
  viewDialogVisible.value = false
}

const handleComplete = async (row) => {
  try {
    await axios.post('/feedback/feedbackStateEdit', {
      feedbackId: row.feedbackId,
      state: '已完成',
      reply: row.reply || ''
    })
    ElMessage.success('操作成功')
    fetchData()
    if (viewDialogVisible.value) {
      viewDialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const submitProcess = async () => {
  if (!processFormRef.value) return
  await processFormRef.value.validate(async (valid) => {
    if (valid) {
      processLoading.value = true
      try {
        await axios.post('/feedback/feedbackStateEdit', {
          feedbackId: currentFeedback.value.feedbackId,
          state: processForm.state,
          reply: processForm.reply
        })
        ElMessage.success('处理成功')
        processDialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('处理失败')
      } finally {
        processLoading.value = false
      }
    }
  })
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await axios.get('/feedback/feedbackList', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...filterForm
      }
    })
    tableData.value = response.data.data.items
    total.value = response.data.data.total
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 初始加载
fetchData()
</script>

<style scoped>
.feedback {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
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

.feedback-detail {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
}

.detail-item .label {
  font-weight: bold;
  margin-right: 10px;
}

.detail-item .content {
  margin-top: 5px;
  white-space: pre-wrap;
  line-height: 1.5;
}
</style> 