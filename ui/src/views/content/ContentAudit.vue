<template>
  <div class="content-audit">
    <div class="page-header">
      <h2>内容审核</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="标题">
          <el-input v-model="filterForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="filterForm.category" placeholder="请选择分类" clearable>
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
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
        <el-table-column prop="title" label="标题" min-width="100" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="createAt" label="提交时间" width="240" />
        <el-table-column prop="state" label="状态" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                type="primary"
                link
                @click="handleAudit(row)"
              >
                审核
              </el-button>
              <el-button
                type="primary"
                link
                @click="handlePreview(row)"
              >
                预览
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      title="内容审核"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="auditFormRef"
        :model="auditForm"
        :rules="auditRules"
        label-width="80px"
      >
        <el-form-item label="审核结果" prop="state">
          <el-radio-group v-model="auditForm.state">
            <el-radio label="已发布">通过</el-radio>
            <el-radio label="已拒绝">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit" :loading="auditLoading">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预览对话框 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="内容预览"
      width="800px"
      class="preview-dialog"
      :close-on-click-modal="false"
    >
      <div class="preview-content">
        <div class="preview-header">
          <h2 class="preview-title">{{ previewData.title }}</h2>
          <div class="preview-meta">
            <p class="meta-item">
              <i class="el-icon-user"></i>
              <span class="meta-label">作者：</span>
              <span class="meta-value">{{ previewData.author }}</span>
            </p>
            <p class="meta-item">
              <i class="el-icon-folder"></i>
              <span class="meta-label">分类：</span>
              <span class="meta-value">{{ previewData.category }}</span>
            </p>
            <p class="meta-item">
              <i class="el-icon-time"></i>
              <span class="meta-label">提交时间：</span>
              <span class="meta-value">{{ previewData.createAt }}</span>
            </p>
            <p class="meta-item">
              <i class="el-icon-document"></i>
              <span class="meta-label">状态：</span>
              <span class="meta-value">{{ previewData.state }}</span>
            </p>
          </div>
        </div>
        <div class="preview-body markdown-body" v-html="previewData.content"></div>
      </div>
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

const categories = [
  { label: '新闻', value: '新闻' },
  { label: '公告', value: '公告' },
  { label: '活动', value: '活动' }
]

const filterForm = reactive({
  title: '',
  category: '',
  state: '待审核'

})

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

// 审核相关
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref(null)
const currentContent = ref(null)

const auditForm = reactive({
  state: '已发布'
})

const auditRules = {
  state: [{ required: true, message: '请选择审核结果', trigger: 'change' }]
}

const handleAudit = (row) => {
  currentContent.value = row
  auditForm.state = '已发布'
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  if (!auditFormRef.value) return
  
  await auditFormRef.value.validate(async (valid) => {
    if (valid) {
      auditLoading.value = true
      try {
        const response = await axios.post('/article/auditStateEdit', {
          articleId: currentContent.value.articleId,
          state: auditForm.state
        })
        if (response.data.code === 200) {
          ElMessage.success('审核成功')
          auditDialogVisible.value = false
          fetchData()
        } else {
          ElMessage.error(response.data.msg || '审核失败')
        }
      } catch (error) {
        console.error('审核失败:', error)
        ElMessage.error('审核失败')
      } finally {
        auditLoading.value = false
      }
    }
  })
}

const previewDialogVisible = ref(false)
const previewData = ref({})

const handlePreview = (row) => {
  if (!row) {
    ElMessage.error('数据不存在')
    return
  }
  previewData.value = row
  previewDialogVisible.value = true
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await axios.get('/article/auditList', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...filterForm
      }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data.items
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取数据失败')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 初始加载
fetchData()
</script>

<style scoped>
.content-audit {
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

.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.preview-dialog :deep(.el-dialog__header) {
  margin: 0;
  padding: 20px;
  border-bottom: 1px solid #eee;
  background-color: #f8f9fa;
}

.preview-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
}

.preview-content {
  padding: 0;
}

.preview-header {
  padding: 24px;
  border-bottom: 1px solid #eee;
  background-color: #f8f9fa;
}

.preview-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 20px 0;
  color: #1a1a1a;
  line-height: 1.4;
}

.preview-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  color: #666;
  font-size: 14px;
}

.meta-item {
  display: flex;
  align-items: center;
  margin: 0;
  gap: 8px;
}

.meta-item i {
  font-size: 16px;
  color: #409EFF;
}

.meta-label {
  color: #909399;
  min-width: 70px;
}

.meta-value {
  color: #606266;
  font-weight: 500;
}

.preview-body {
  padding: 24px;
  min-height: 200px;
  max-height: 600px;
  overflow-y: auto;
  background-color: #fff;
}

.markdown-body {
  font-size: 16px;
  line-height: 1.8;
  color: #24292e;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4),
.markdown-body :deep(h5),
.markdown-body :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
  color: #1a1a1a;
}

.markdown-body :deep(p) {
  margin-top: 0;
  margin-bottom: 16px;
}

.markdown-body :deep(img) {
  max-width: 100%;
  box-sizing: border-box;
  border-radius: 4px;
  margin: 16px 0;
}

.markdown-body :deep(pre) {
  padding: 16px;
  overflow: auto;
  font-size: 85%;
  line-height: 1.45;
  background-color: #f6f8fa;
  border-radius: 6px;
  margin: 16px 0;
}

.markdown-body :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 85%;
  background-color: rgba(27,31,35,0.05);
  border-radius: 6px;
}

.markdown-body :deep(blockquote) {
  margin: 16px 0;
  padding: 0 16px;
  color: #6a737d;
  border-left: 4px solid #dfe2e5;
}

.markdown-body :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin: 16px 0;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  padding: 8px 16px;
  border: 1px solid #dfe2e5;
}

.markdown-body :deep(th) {
  background-color: #f6f8fa;
  font-weight: 600;
}
</style> 