<template>
  <div class="content-edit">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑内容' : '新建内容' }}</h2>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>

        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>

        <el-form-item label="状态" prop="state">
          <el-select v-model="form.state" placeholder="请选择状态">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="内容" prop="content">
          <div class="editor-container">
            <Toolbar
              style="border-bottom: 1px solid #dcdfe6"
              :editor="editorRef"
              :defaultConfig="toolbarConfig"
              mode="default"
            />
            <Editor
              style="height: 500px"
              v-model="form.content"
              :defaultConfig="editorConfig"
              mode="default"
              @onCreated="handleEditorCreated"
            />
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            {{ isEdit ? '保存' : '发布' }}
          </el-button>
          <el-button @click="$router.push('/content')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, shallowRef, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const editorRef = shallowRef()

const isEdit = ref(!!route.params.id)

const categories = [
  { label: '新闻', value: '新闻' },
  { label: '公告', value: '公告' },
  { label: '活动', value: '活动' }
]

const statusOptions = [
  { label: '草稿', value: '草稿' },
  { label: '待审核', value: '待审核' },
  { label: '已发布', value: '已发布' },
  { label: '已拒绝', value: '已拒绝' }
]

const form = reactive({
  title: '',
  category: '',
  author: '',
  content: '',
  state: '草稿'
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  state: [{ required: true, message: '请选择状态', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

// 编辑器配置
const toolbarConfig = {}
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/upload',
      headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      customInsert(res, insertFn) {
        insertFn(res.data.url)
      }
    }
  }
}

const handleEditorCreated = (editor) => {
  editorRef.value = editor
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        if (isEdit.value) {
          await axios.put(`/article/add/${route.params.id}`, form)
          ElMessage.success('更新成功')
        } else {
          await axios.post('/article/add', form)
          ElMessage.success('创建成功')
        }
        router.push('/content')
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 获取内容详情
const fetchContent = async () => {
  if (!isEdit.value) return
  
  try {
    const response = await axios.get(`/article/add${route.params.id}`)
    Object.assign(form, response.data)
  } catch (error) {
    ElMessage.error('获取内容失败')
    router.push('/content')
  }
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

// 初始加载
fetchContent()
</script>

<style scoped>
.content-edit {
  min-height: 100%;
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
}

.page-header {
  margin-bottom: 20px;
  width: 100%;
  max-width: 1200px;
  flex-shrink: 0;
}

.el-card {
  width: 100%;
  max-width: 1200px;
  flex: 1;
  margin-bottom: 20px;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  min-height: 500px;
}

:deep(.el-form) {
  max-width: 100%;
  overflow: visible;
}

:deep(.el-form-item__content) {
  max-width: 100%;
  overflow: visible;
}
</style> 