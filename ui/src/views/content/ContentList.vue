<template>
  <div class="content-list">
    <div class="page-header">
      <h2>内容列表</h2>
      <el-button type="primary" @click="$router.push('/content/edit')">
        新建内容
      </el-button>
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
        <el-form-item label="状态">
          <el-select v-model="filterForm.state" placeholder="请选择状态" clearable>
            <el-option label="草稿" value="草稿" />
            <el-option label="待审核" value="待审核" />
            <el-option label="已发布" value="已发布" />
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
        <el-table-column prop="title" label="标题" min-width="100" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="createAt" label="创建时间" width="240" />
        <el-table-column prop="state" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.state)">
              {{ getStatusText(row.state) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
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
                type="primary"
                link
                @click="handlePreview(row)"
              >
                预览
              </el-button>
              <el-button
                type="danger"
                link
                :loading="deletingId === row.articleId"
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

    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'edit' ? '编辑内容' : '预览内容'"
      width="600px"
    >
      <el-form v-if="dialogMode === 'edit'" :model="currentRecord" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="currentRecord.title" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="currentRecord.category" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="currentRecord.author" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="currentRecord.content" />
        </el-form-item>
      </el-form>
      <div v-else>
        <p><b>标题：</b>{{ currentRecord.title }}</p>
        <p><b>分类：</b>{{ currentRecord.category }}</p>
        <p><b>作者：</b>{{ currentRecord.author }}</p>
        <p><b>内容：</b>{{ currentRecord.content }}</p>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="dialogMode === 'edit'" type="primary" @click="saveEdit">保存</el-button>
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

const categories = [
  { label: '新闻', value: '新闻' },
  { label: '公告', value: '公告' },
  { label: '活动', value: '活动' }
]

const filterForm = reactive({
  title: '',
  category: '',
  state: ''
})

// 对话框的显示状态
const dialogVisible = ref(false)
// 对话框的模式：'view'表示预览模式，'edit'表示编辑模式
const dialogMode = ref('edit')
// 当前正在编辑或预览的记录数据
const currentRecord = ref({})

const deletingId = ref(null)

const getStatusType = (status) => {
  const map = {
    '草稿': 'info',
    '待审核': 'warning',
    '已发布': 'success',
    '已拒绝': 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (state) => {
  return state || '未知状态'
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

const handleEdit = (row) => {
  dialogMode.value = 'edit'
  currentRecord.value = { ...row }
  dialogVisible.value = true
}

const handlePreview = (row) => {
  dialogMode.value = 'view'
  currentRecord.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该内容吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    deletingId.value = row.articleId
    console.log(row.articleId)
    // 推荐写法1：如果后端支持DELETE
     await axios.delete('/article/remove?articleId=' + row.articleId)

    // 推荐写法2：如果后端只支持POST
    // await axios.post('/article/remove', { articleId: row.articleId })

    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  } finally {
    deletingId.value = null
  }
}

const saveEdit = async () => {
  // 这里可以调用后端API保存
   await axios.post('/article/edit', currentRecord.value)
  ElMessage.success('保存成功（示例）')
  dialogVisible.value = false
  fetchData()
}

const fetchData = async () => {
  loading.value = true
  try {
    const response = await axios.get('/article/list', {
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...filterForm
      }
    })
    console.log(response.data)
    if (response.data.code === 200) {
      tableData.value = response.data.data.items
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.msg || '获取数据失败')
    }
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
.content-list {
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
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

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  margin-bottom: 20px;
  height: calc(100% - 180px);
  display: flex;
  flex-direction: column;
}

.table-card :deep(.el-card__body) {
  flex: 1;
  overflow: auto;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 