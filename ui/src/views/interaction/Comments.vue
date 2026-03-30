<template>
  <div class="comments">
    <div class="page-header">
      <h2>评论管理</h2>
    </div>

    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="内容标题">
          <el-input v-model="filterForm.title" placeholder="请输入内容标题" clearable />
        </el-form-item>
        <el-form-item label="评论内容">
          <el-input v-model="filterForm.content" placeholder="请输入评论内容" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.state" placeholder="请选择状态" clearable>
            <el-option label="待审核" value="待审核" />
            <el-option label="已通过" value="已通过" />
            <el-option label="已拒绝" value="已拒绝" />
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
        <el-table-column prop="title" label="内容标题" min-width="150" />
        <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
        <el-table-column prop="user" label="评论用户" width="120" />
        <el-table-column prop="createAt" label="评论时间" width="150" />
        <el-table-column prop="state" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.state)">
              {{ getStatusText(row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                v-if="row.state === '待审核'"
                type="success"
                link
                @click="handleAudit(row, '已通过')"
              >
                通过
              </el-button>
              <el-button
                v-if="row.state === '待审核'"
                type="danger"
                link
                @click="handleAudit(row, '已拒绝')"
              >
                拒绝
              </el-button>
              <el-button
                type="primary"
                link
                @click="handlePreview(row)"
              >
                预览
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

    <!-- 审核对话框 -->
    <el-dialog
      v-model="auditDialogVisible"
      :title="auditForm.state === '已通过' ? '通过评论' : '拒绝评论'"
      width="500px"
    >
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
      title="评论详情"
      width="500px"
    >
      <div v-if="currentPreview">
        <p><b>内容标题：</b>{{ currentPreview.title }}</p>
        <p><b>评论内容：</b>{{ currentPreview.content }}</p>
        <p><b>评论用户：</b>{{ currentPreview.user }}</p>
        <p><b>评论时间：</b>{{ currentPreview.createAt }}</p>
        <p><b>状态：</b>{{ getStatusText(currentPreview.state) }}</p>
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
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

const filterForm = reactive({
  title: '',
  content: '',
  state: ''
})

const getStatusType = (status) => {
  const map = {
    '待审核': 'warning',
    '已通过': 'success',
    '已拒绝': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    '待审核': '待审核',
    '已通过': '已通过',
    '已拒绝': '已拒绝'
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

// 审核相关
const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const auditFormRef = ref(null)
const currentComment = ref(null)

const auditForm = reactive({
  state: ''
})

const handleAudit = (row, status) => {
  currentComment.value = row
  auditForm.state = status
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  auditLoading.value = true
  try {
    await axios.post('/comment/commentStateEdit',{
      commentId: currentComment.value.commentId,
      state: auditForm.state
    })
    ElMessage.success('审核成功')
    auditDialogVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('审核失败')
  } finally {
    auditLoading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await axios.delete('/comment/remove?commentId='+row.commentId)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const previewDialogVisible = ref(false)
const currentPreview = ref(null)

const handlePreview = (row) => {
  currentPreview.value = row
  previewDialogVisible.value = true
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await axios.get('/comment/commentList', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        title: filterForm.title || null,
        content: filterForm.content,
        state: filterForm.state
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
.comments {
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
</style> 