<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical"
        :router="true"
        :collapse="isCollapse"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Monitor /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        
        <el-sub-menu index="/content">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>内容管理</span>
          </template>
          <el-menu-item index="/content">内容列表</el-menu-item>
          <el-menu-item index="/content/edit">新建内容</el-menu-item>
          <el-menu-item index="/content/audit">内容审核</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/interaction">
          <template #title>
            <el-icon><ChatDotRound /></el-icon>
            <span>互动功能</span>
          </template>
          <el-menu-item index="/comments">评论管理</el-menu-item>
          <el-menu-item index="/feedback">反馈管理</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="/advertisement">
          <template #title>
            <el-icon><Picture /></el-icon>
            <span>广告管理</span>
          </template>
          <el-menu-item index="/advertisement">广告列表</el-menu-item>
          <el-menu-item index="/advertisement/edit">新建广告</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>

    <el-dialog
      v-model="profileDialogVisible"
      title="个人信息"
      width="30%"
      :close-on-click-modal="false"
    >
      <div class="profile-content">
        <div class="profile-item">
          <span class="label">用户名：</span>
          <span>{{ username }}</span>
        </div>
        <div class="profile-item">
          <span class="label">角色：</span>
          <span>{{ userRole }}</span>
        </div>
      </div>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import axios from 'axios'
import {
  Monitor,
  Document,
  ChatDotRound,
  Picture,
  Fold,
  Expand
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const username = ref('')
const userRole = ref('')
const profileDialogVisible = ref(false)

// 获取用户信息
const getUserInfo = async () => {
  try {
    const token = localStorage.getItem('token')
    if (!token) {
      router.push('/login')
      return
    }
    
    const response = await axios.get('/user/info', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    
    if (response.data.code === 200) {
      const userInfo = response.data.data
      username.value = userInfo.username
      userRole.value = userInfo.role
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息出错：', error)
    ElMessage.error('获取用户信息失败')
  }
}

onMounted(() => {
  getUserInfo()
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      localStorage.removeItem('token')
      router.push('/login')
    } catch {
      // 用户取消操作
    }
  } else if (command === 'profile') {
    profileDialogVisible.value = true
    await getUserInfo()
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.el-aside {
  background-color: #304156;
  transition: width 0.3s;
  height: 100vh;
  overflow-y: auto;
}

.el-menu {
  border-right: none;
  height: 100%;
}

.el-header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
  line-height: 60px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
  height: calc(100vh - 60px);
  overflow-y: auto;
}

.profile-content {
  padding: 20px;
}

.profile-item {
  margin-bottom: 15px;
  font-size: 14px;
}

.profile-item .label {
  font-weight: bold;
  margin-right: 10px;
  color: #606266;
}
</style> 