<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2>门户网站管理系统</h2>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="role">
          <el-select v-model="loginForm.role" placeholder="请选择身份" style="width: 100%">
            <el-option label="管理员" value="管理员" />
            <el-option label="用户" value="用户" />
          </el-select>
        </el-form-item>
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from 'axios'

const router = useRouter()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  role: '',
  username: '',
  password: ''
})

const rules = {
  role: [{ required: true, message: '请选择身份', trigger: 'change' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      if (loginForm.role !== '管理员') {
        ElMessage.error('您的权限不够，只有管理员可以登录系统')
        return
      }

      loading.value = true
      try {
        const formData = new FormData();
        formData.append('username', loginForm.username);
        formData.append('password', loginForm.password);
        formData.append('role', loginForm.role);

        const response = await axios.post('/user/login', formData);
        
        if (response.data && response.data.data) {
          localStorage.setItem('token', response.data.data)
          ElMessage.success('登录成功')
          router.push('/')
        } else {
          ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
        }
      } catch (error) {
        console.error('登录错误：', error)
        ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
}

.login-card {
  width: 400px;
}

.login-card :deep(.el-card__header) {
  text-align: center;
}

h2 {
  margin: 0;
  color: #409EFF;
}
</style> 