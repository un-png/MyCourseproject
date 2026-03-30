<template>
  <div class="advertisement-edit">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑广告' : '新建广告' }}</h2>
    </div>

    <el-card>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="广告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入广告标题" />
        </el-form-item>

        <el-form-item label="广告位" prop="position">
          <el-select v-model="form.position" placeholder="请选择广告位">
            <el-option
              v-for="item in positions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="广告类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio label="image">图片</el-radio>
            <el-radio label="video">视频</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item
          v-if="form.type === 'image'"
          label="广告图片"
          prop="mediaUrl"
        >
          <el-input v-model="form.mediaUrl" placeholder="请输入图片地址（以http://或https://开头）">
            <template #append>
              <el-button @click="previewImage">预览</el-button>
            </template>
          </el-input>
          <div class="preview-image" v-if="form.mediaUrl && (form.mediaUrl.startsWith('http://') || form.mediaUrl.startsWith('https://'))">
            <img :src="form.mediaUrl" class="ad-image" @error="handleImageError" />
          </div>
          <div class="upload-tip">请输入完整的图片URL地址，以http://或https://开头</div>
        </el-form-item>

        <el-form-item
          v-if="form.type === 'video'"
          label="视频链接"
          prop="mediaUrl"
        >
          <el-input v-model="form.mediaUrl" placeholder="请输入视频链接" />
        </el-form-item>

        <el-form-item label="链接地址" prop="link">
          <el-input v-model="form.link" placeholder="请输入链接地址" />
        </el-form-item>

        <el-form-item label="投放时间" prop="timeRange">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="开始时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 200px; margin-right: 10px"
          />
          <span style="margin: 0 10px">至</span>
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="结束时间"
            value-format="YYYY-MM-DDTHH:mm:ss"
            style="width: 200px"
          />
        </el-form-item>

        <el-form-item label="目标用户" prop="targetUser">
          <el-select v-model="form.targetUser" placeholder="请选择目标用户">
            <el-option label="全部用户" value="全部用户" />
            <el-option label="新用户" value="新用户" />
            <el-option label="老用户" value="老用户" />
            <el-option label="VIP用户" value="VIP用户" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            保存
          </el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const isEdit = computed(() => !!route.params.id)

const positions = [
  { label: '首页轮播', value: '首页轮播' },
  { label: '首页侧边', value: '首页侧边' },
  { label: '文章页侧边', value: '文章页侧边' },
  { label: '文章页底部', value: '文章页底部' }
]

const form = reactive({
  title: '',
  position: '',
  type: 'image',
  mediaUrl: '',
  link: '',
  startTime: '',
  endTime: '',
  targetUser: 'all',
  status: '待投放'
})

const rules = {
  title: [{ required: true, message: '请输入广告标题', trigger: 'blur' }],
  position: [{ required: true, message: '请选择广告位', trigger: 'change' }],
  type: [{ required: true, message: '请选择广告类型', trigger: 'change' }],
  mediaUrl: [
    { required: true, message: '请输入媒体地址', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value && !value.startsWith('http://') && !value.startsWith('https://')) {
          callback(new Error('请输入有效的URL地址，以http://或https://开头'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ],
  link: [{ required: true, message: '请输入链接地址', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
  targetUser: [{ required: true, message: '请选择目标用户', trigger: 'change' }]
}

const getPositionSize = (position) => {
  const map = {
    home_banner: '1920x500',
    home_sidebar: '300x250',
    article_sidebar: '300x250',
    article_bottom: '728x90'
  }
  return map[position] || ''
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const data = {
          ...form,
          createAt: new Date().toISOString().slice(0, 19)
        }

        if (isEdit.value) {
          await axios.put(`/advertisement/add/${route.params.id}`, data)
          ElMessage.success('更新成功')
        } else {
          await axios.post('/advertisement/add', data)
          ElMessage.success('创建成功')
        }
        router.push('/advertisement')
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      } finally {
        loading.value = false
      }
    }
  })
}

// 获取广告详情
const fetchAdvertisement = async () => {
  if (!isEdit.value) return
  
  try {
    const response = await axios.get(`/advertisement/add/${route.params.id}`)
    const data = response.data
    Object.assign(form, data)
  } catch (error) {
    ElMessage.error('获取广告详情失败')
    router.push('/advertisement')
  }
}

// 初始加载
fetchAdvertisement()

const previewImage = () => {
  if (!form.mediaUrl) {
    ElMessage.warning('请先输入图片地址')
    return
  }
  if (!form.mediaUrl.startsWith('http://') && !form.mediaUrl.startsWith('https://')) {
    ElMessage.error('请输入有效的URL地址，以http://或https://开头')
    return
  }
  // 可以在这里添加图片预览逻辑
}

const handleImageError = () => {
  ElMessage.error('图片加载失败，请检查地址是否正确')
}
</script>

<style scoped>
.advertisement-edit {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.preview-image {
  margin-top: 10px;
}

.ad-image {
  max-width: 300px;
  max-height: 150px;
  object-fit: contain;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}
</style> 