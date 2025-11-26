// 个人中心页面
<template>
  <div class="profile-page">
    <div class="profile-container">
      <div class="profile-header">
        <h1>个人资料</h1>
        <p>管理您的个人信息和账户设置</p>
      </div>
      
      <div class="profile-content" v-loading="loading">
        <!-- 左侧用户信息展示区 - 始终保持可见 -->
        <div class="profile-sidebar">
          <div class="user-info-card">
            <!-- 添加个人信息标题 -->
            <h3 class="card-title">个人信息</h3>
            
            <!-- 点击头像即可更换头像 -->
            <div class="user-avatar" @click="uploadAvatar" style="cursor: pointer; position: relative;">
              <img :src="profile.avatar || '/default-avatar.png'" alt="用户头像" />
              <!-- 移除头像编辑图标 -->
            </div>
            
            <h2 class="user-name"><span class="info-label">用户名：</span>{{ profile.username || '未设置用户名' }}</h2>
            <p class="user-bio"><span class="info-label">个人简介：</span>{{ profile.bio || '这个人很懒，什么都没有留下~' }}</p>
            <div class="user-contact">
              <p v-if="profile.email"><i class="el-icon-message"></i> <span class="info-label">邮箱：</span>{{ profile.email }}</p>
              <p v-if="profile.phone"><i class="el-icon-phone"></i> <span class="info-label">手机号：</span>{{ profile.phone }}</p>
            </div>
            <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
          </div>
        </div>
        
        <!-- 右侧信息修改区 - 仅切换内部内容 -->
        <div class="profile-main">
          <!-- 标签页切换 -->
          <div class="profile-tabs">
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'info' }"
              @click="activeTab = 'info'"
              :disabled="loading || saving"
            >
              基本信息
            </button>
            <button 
              class="tab-btn" 
              :class="{ active: activeTab === 'password' }"
              @click="activeTab = 'password'"
              :disabled="loading || saving"
            >
              修改密码
            </button>
          </div>
          
          <!-- 基本信息标签页 -->
          <div class="profile-section" v-show="activeTab === 'info'">
            <h2>编辑个人信息</h2>
            <div class="profile-form">
              <div class="form-group">
                <label>用户名 <span class="required">*</span></label>
                <div class="input-with-status">
                  <input 
                    v-model="profile.username" 
                    type="text" 
                    placeholder="请输入用户名" 
                    maxlength="20"
                    :disabled="loading || saving"
                    @input="onUsernameInput"
                    :class="{ 
                      'input-checking': usernameStatus.checking,
                      'input-available': usernameStatus.isAvailable === true,
                      'input-unavailable': usernameStatus.isAvailable === false
                    }"
                  />
                  <div class="status-indicator" v-if="usernameStatus.checking || usernameStatus.message">
                    <span v-if="usernameStatus.checking" class="checking">
                      <i class="el-icon-loading"></i> 检查中...
                    </span>
                    <span v-else-if="usernameStatus.isAvailable === true" class="available">
                      {{ usernameStatus.message }}
                    </span>
                    <span v-else-if="usernameStatus.isAvailable === false" class="unavailable">
                      {{ usernameStatus.message }}
                    </span>
                  </div>
                </div>
                <!-- 移除字符计数器 -->
                <small class="form-help">用户名需要唯一且不能为空，支持中文、字母、数字和下划线</small>
              </div>
              <div class="form-group">
                <label>邮箱</label>
                <input 
                  v-model="profile.email" 
                  type="email" 
                  placeholder="请输入邮箱" 
                  :disabled="loading || saving"
                />
              </div>
              <div class="form-group">
                <label>手机号</label>
                <input 
                  v-model="profile.phone" 
                  type="tel" 
                  placeholder="请输入手机号" 
                  maxlength="11"
                  :disabled="loading || saving"
                />
              </div>
              <div class="form-group">
                <label>个人简介</label>
                <textarea 
                  v-model="profile.bio" 
                  placeholder="介绍一下自己吧..." 
                  rows="4"
                  maxlength="200"
                  :disabled="loading || saving"
                ></textarea>
                <span class="char-count">{{ profile.bio?.length || 0 }}/200</span>
              </div>
            </div>
          </div>
          
          <!-- 修改密码标签页 -->
          <div class="profile-section" v-show="activeTab === 'password'">
            <h2>修改密码</h2>
            <div class="profile-form">
              <div class="form-group">
                <label>当前密码 <span class="required">*</span></label>
                <input 
                  v-model="passwordForm.currentPassword" 
                  type="password" 
                  placeholder="请输入当前密码" 
                  :disabled="loading || savingPassword"
                />
              </div>
              <div class="form-group">
                <label>新密码 <span class="required">*</span></label>
                <input 
                  v-model="passwordForm.newPassword" 
                  type="password" 
                  placeholder="请输入新密码" 
                  :disabled="loading || savingPassword"
                />
                <small class="form-help">密码长度至少6位，可以包含字母、数字和标点符号</small>
              </div>
              <div class="form-group">
                <label>确认新密码 <span class="required">*</span></label>
                <input 
                  v-model="passwordForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入新密码" 
                  :disabled="loading || savingPassword"
                />
              </div>
              <div v-if="passwordStatus.message" class="password-status" :class="passwordStatus.type">
                {{ passwordStatus.message }}
              </div>
            </div>
          </div>
          
          <div class="profile-actions">
      <button 
        class="save-btn" 
        :disabled="loading || saving || savingPassword"
      >
        <span v-if="saving || savingPassword">保存中...</span>
        <span v-else>保存更改</span>
      </button>
      <button 
        class="cancel-btn" 
        @click="activeTab === 'info' ? resetProfile() : resetPasswordForm()"
        :disabled="loading || saving || savingPassword"
      >
        重置
      </button>
    </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { getUserProfile, updateUserProfile, updateUserAvatar, checkUsernameAvailability, changePassword } from '@/api/user.js'
import { fetchUserInfo, updateUserInfo, getCurrentUserInfo } from '@/utils/userStore.js'

const profile = reactive({
  id: null,
  username: '',
  email: '',
  phone: '',
  bio: '',
  avatar: ''
})

// 密码表单数据
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码修改状态
const passwordStatus = reactive({
  type: '', // success, error
  message: ''
})

const originalProfile = ref({})
const loading = ref(false)
const saving = ref(false)
const savingPassword = ref(false)
const activeTab = ref('info')
const avatarInput = ref(null)
const userId = ref('')

// 用户名检测状态
const usernameStatus = reactive({
  checking: false,
  isAvailable: null,
  message: '',
  lastCheckedUsername: ''
})

let usernameCheckTimer = null

// 获取当前用户ID
const getCurrentUserId = () => {
  return localStorage.getItem('userId') || localStorage.getItem('currentUserId') || '6'
}

// 加载用户个人资料
const loadUserProfile = async () => {
  try {
    loading.value = true
    // 获取并设置当前用户ID
    userId.value = getCurrentUserId()
    // CONSOLE LOG REMOVED: console.log('正在从数据库加载用户个人资料...')
    
    // 使用统一的用户信息管理系统
    const userData = await fetchUserInfo()
    
    if (userData) {
      // 更新profile数据，确保使用数据库中的username字段
      Object.assign(profile, {
        id: userData.id,
        username: userData.username,  // 确保使用数据库中的username
        email: userData.email,
        phone: userData.phone,
        bio: userData.bio,
        avatar: userData.avatar
      })
      
      // 保存原始数据用于重置
      originalProfile.value = { ...profile }
      
      // CONSOLE LOG REMOVED: console.log('用户个人资料加载成功，用户名来自数据库:', profile.username)
      
    } else {
      // CONSOLE LOG REMOVED: console.warn('无法获取用户数据，使用默认数据')
      setDefaultProfile()
    }
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('❌ 加载用户个人资料失败:', error)
    ElMessage.error('加载个人资料失败，请刷新页面重试')
    setDefaultProfile()
  } finally {
    loading.value = false
  }
}

// 设置默认个人资料
const setDefaultProfile = () => {
  const userId = getCurrentUserId()
  Object.assign(profile, {
    id: userId,
    username: localStorage.getItem('userUsername') || '苏黎世的从前',
    email: '',
    phone: '',
    bio: localStorage.getItem('userBio') || '热爱音乐，分享美好。每一首歌都是心灵的触动，每一个音符都是情感的流露。',
    avatar: localStorage.getItem('userAvatar') || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100'
  })
  originalProfile.value = { ...profile }
}

function uploadAvatar() {
  // 触发文件选择
  avatarInput.value.click()
}

async function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  
  try {
    // 验证文件类型和大小
    if (!file.type.startsWith('image/')) {
      ElMessage.error('请选择图片文件')
      return
    }
    
    if (file.size > 5 * 1024 * 1024) { // 5MB限制
      ElMessage.error('文件大小不能超过5MB')
      return
    }
    
    saving.value = true
    
    // 创建FormData
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId.value)
    
    // 上传头像到MinIO
    const uploadResponse = await updateUserAvatar(formData)
    
    // 检查响应是否成功（支持多种格式）
    if (uploadResponse && (uploadResponse.code === 200 || uploadResponse.code === 0 || uploadResponse.success === true)) {
      // 获取上传后的头像URL（支持多种字段名）
      const avatarUrl = uploadResponse.data?.avatar || uploadResponse.data || uploadResponse.url
      
      if (!avatarUrl) {
        throw new Error('无法获取头像URL')
      }
      
      // 更新用户资料中的头像
      profile.avatar = avatarUrl
      
      // 立即保存头像信息
      await saveProfile()
      
      ElMessage.success('头像上传成功！')
    } else {
      throw new Error(uploadResponse?.message || '头像上传失败')
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('头像上传失败:', error)
    ElMessage.error(`上传失败: ${error.message || '未知错误'}`)
  } finally {
    saving.value = false
    e.target.value = ''
  }
}



// 用户名输入处理
const onUsernameInput = () => {
  const currentUsername = profile.username?.trim()
  
  if (usernameCheckTimer) {
    clearTimeout(usernameCheckTimer)
  }
  
  // 重置状态
  usernameStatus.checking = false
  usernameStatus.isAvailable = null
  usernameStatus.message = ''
  
  // 如果用户名为空，不进行检查
  if (!currentUsername) {
    return
  }
  
  // 如果用户名与原始用户名相同，不进行检查
  if (currentUsername === originalProfile.value.username) {
    usernameStatus.isAvailable = true
    usernameStatus.message = '当前用户名'
    return
  }
  
  usernameCheckTimer = setTimeout(() => {
    checkUsernameAvailabilityDebounced(currentUsername)
  }, 500)
}

// 检查用户名可用性
const checkUsernameAvailabilityDebounced = async (username) => {
  if (!username || username === usernameStatus.lastCheckedUsername) {
    return
  }
  
  // 基本格式验证
  const usernameRegex = /^[\u4e00-\u9fa5a-zA-Z0-9_]+$/
  if (!usernameRegex.test(username)) {
    usernameStatus.isAvailable = false
    usernameStatus.message = '用户名格式不正确'
    return
  }
  
  try {
    usernameStatus.checking = true
    usernameStatus.lastCheckedUsername = username
    
    // CONSOLE LOG REMOVED: console.log('检查用户名可用性:', username)
    const currentUserId = getCurrentUserId()
    const response = await checkUsernameAvailability(username, currentUserId)
    
    // CONSOLE LOG REMOVED: console.log('用户名检查响应:', response)
    
    if (response && response.code === 200) {
      usernameStatus.isAvailable = response.data
      usernameStatus.message = response.message || (response.data ? '用户名可用' : '用户名已被使用')
    } else {
      usernameStatus.isAvailable = false
      usernameStatus.message = response?.message || '检查失败'
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('检查用户名失败:', error)
    usernameStatus.isAvailable = null
    usernameStatus.message = '检查失败，请稍后重试'
  } finally {
    usernameStatus.checking = false
  }
}

// 表单验证
const validateProfile = () => {
  if (!profile.username || profile.username.trim().length === 0) {
    ElMessage.error('用户名不能为空')
    return false
  }
  
  if (profile.username.length > 20) {
    ElMessage.error('用户名不能超过20个字符')
    return false
  }
  
  // 允许中文、字母、数字和下划线，不允许特殊字符
  if (!/^[\u4e00-\u9fa5a-zA-Z0-9_]+$/.test(profile.username)) {
    ElMessage.error('用户名只能包含中文、字母、数字和下划线')
    return false
  }
  
  // 检查用户名是否可用
  const currentUsername = profile.username.trim()
  if (currentUsername !== originalProfile.value.username && usernameStatus.isAvailable === false) {
    ElMessage.error('用户名已被使用，请选择其他用户名')
    return false
  }
  
  if (profile.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profile.email)) {
    ElMessage.error('邮箱格式不正确')
    return false
  }
  
  if (profile.phone && !/^1[3-9]\d{9}$/.test(profile.phone)) {
    ElMessage.error('手机号格式不正确')
    return false
  }
  
  if (profile.bio && profile.bio.length > 200) {
    ElMessage.error('个人简介不能超过200个字符')
    return false
  }
  
  return true
}

async function saveProfile() {
  if (!validateProfile()) {
    return
  }
  
  try {
    saving.value = true
    const userId = getCurrentUserId()    
    const profileData = {
      username: profile.username.trim(),
      email: profile.email.trim(),
      phone: profile.phone.trim(),
      bio: profile.bio.trim(),
      avatar: profile.avatar
    }
    
    const response = await updateUserProfile(userId, profileData)
    
    if (response && response.code === 200) {
      updateUserInfo({
        id: profile.id,
        username: profile.username,  
        email: profile.email,
        phone: profile.phone,
        bio: profile.bio,
        avatar: profile.avatar
      })
      
      // 更新原始数据
      originalProfile.value = { ...profile }
      
      // CONSOLE LOG REMOVED: console.log('个人资料保存成功，用户名已同步更新:', profile.username)
      ElMessage.success('个人资料保存成功！')
      
    } else {
      throw new Error(response?.message || '保存失败')
    }
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('保存个人资料失败:', error)
    
    // 处理不同类型的错误
    let errorMessage = '保存失败，请重试'
    
    if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    // 特殊处理用户名重复的情况
    if (errorMessage.includes('用户名已被使用') || errorMessage.includes('用户名已存在')) {
      ElMessage.error(`${errorMessage}`)
    } else if (errorMessage.includes('用户名')) {
      ElMessage.error(`${errorMessage}`)
    } else {
      ElMessage.error(`${errorMessage}`)
    }
  } finally {
    saving.value = false
  }
}

function resetProfile() {
  Object.assign(profile, originalProfile.value)
  ElMessage.info('已重置为原始数据')
}

// 重置密码表单
function resetPasswordForm() {
  passwordForm.currentPassword = ''
  passwordForm.newPassword = ''
  passwordForm.confirmPassword = ''
  passwordStatus.type = ''
  passwordStatus.message = ''
  ElMessage.info('密码表单已重置')
}

// 验证密码表单
const validatePasswordForm = () => {
  if (!passwordForm.currentPassword) {
    ElMessage.error('请输入当前密码')
    return false
  }
  
  if (!passwordForm.newPassword) {
    ElMessage.error('请输入新密码')
    return false
  }
  
  // 密码强度验证
  if (passwordForm.newPassword.length < 6) {
    ElMessage.error('新密码长度至少6位')
    return false
  }
  
  // 允许字母、数字和常用标点符号
  if (!/^[a-zA-Z0-9!@#$%^&*(),.?":{}|<>]+$/.test(passwordForm.newPassword)) {
    ElMessage.error('新密码只能包含字母、数字和常用标点符号')
    return false
  }
  
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return false
  }
  
  if (passwordForm.currentPassword === passwordForm.newPassword) {
    ElMessage.error('新密码不能与当前密码相同')
    return false
  }
  
  return true
}

// 修改密码
async function savePassword() {
  if (!validatePasswordForm()) {
    return
  }
  
  try {
    savingPassword.value = true
    passwordStatus.message = ''
    passwordStatus.type = ''
    
    const userId = getCurrentUserId()
    
    // CONSOLE LOG REMOVED: console.log('正在修改密码...', { userId })
    
    // 调用实际的修改密码API
    const response = await changePassword(
      userId, 
      passwordForm.currentPassword, 
      passwordForm.newPassword
    )
    
    // 检查响应是否成功
    if (response && (response.code === 200 || response.code === 0 || response.success === true)) {
      passwordStatus.type = 'success'
      passwordStatus.message = '密码修改成功！'
      
      // CONSOLE LOG REMOVED: console.log('密码修改成功')
      ElMessage.success('密码修改成功！')
    } else {
      throw new Error(response?.message || '密码修改失败')
    }
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('修改密码失败:', error)
    passwordStatus.type = 'error'
    passwordStatus.message = error.message || '修改密码失败，请重试'
    ElMessage.error(`密码修改失败: ${error.message || '未知错误'}`)
  } finally {
    savingPassword.value = false
  }
}

// 页面挂载时加载个人资料
onMounted(() => {
  loadUserProfile()
})

// 切换背景设置显示状态
const toggleBackgroundSettings = () => {
  showBackgroundSettings.value = !showBackgroundSettings.value
}
</script>

<style scoped>
html, body {
  height: 100%;
  overflow: hidden;
  margin: 0;
  padding: 0;
}

.profile-page {
  height: 100vh;
  background: var(--background);
  padding: 0 20px;
  overflow: hidden;
  display: flex;
  align-items: flex-start;
  justify-content: center;
}

.profile-container {
  width: 100%;
  max-width: 1200px;
  max-height: 98vh;
  margin: 0 auto;
  background: var(--background-card);
  border-radius: 8px;
  padding: 10px 20px 15px 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  margin-top: 10px;
}

.profile-header {
  text-align: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--border-color);
}

.profile-header h1 {
  color: #333;
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 5px 0;
}

.profile-header p {
  color: #999;
  font-size: 12px;
  margin: 0;
}

/* 标签页样式 */
.profile-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
  border-bottom: 1px solid var(--border-color);
}

.tab-btn {
  padding: 10px 20px;
  background: transparent;
  border: none;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
}

.tab-btn:hover:not(:disabled) {
  color: var(--primary-color);
}

.tab-btn.active {
  color: var(--primary-color);
  border-bottom-color: var(--primary-color);
}

.tab-btn:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

/* 密码状态提示 */
.password-status {
  padding: 10px 15px;
  border-radius: 4px;
  margin-top: 10px;
  font-size: 14px;
}

.password-status.success {
  background: #f0f9ff;
  color: #67c23a;
  border: 1px solid #e1f3d8;
}

.password-status.error {
  background: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fde2e2;
}

/* 左右分栏布局 */
.profile-content {
  display: flex;
  gap: 20px;
  flex: 1;
  min-height: 0;
}

/* 左侧用户信息展示区 */
.profile-sidebar {
  width: 320px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
}

.user-info-card {
  background: var(--background-card);
  border-radius: 12px;
  padding: 30px 25px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  border: 1px solid var(--border-color);
  /* 固定尺寸，不随右侧内容变化 */
  flex-shrink: 0;
  min-height: 500px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 个人信息标题样式 */
.card-title {
  color: var(--primary-color);
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 2px solid var(--border-color);
}

/* 头像编辑图标样式已移除 */

/* 移除原来的上传按钮样式 */
.upload-btn {
  display: none;
}

.user-avatar {
  width: 160px;
  height: 160px;
  margin: 0 auto 20px;
  border-radius: 50%;
  overflow: hidden;
  border: 4px solid #ff6b96;
  box-shadow: 0 6px 18px rgba(255, 107, 150, 0.4);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-name {
  color: var(--text-primary);
  font-size: 22px;
  font-weight: 600;
  margin: 0 0 12px 0;
}

.info-label {
  color: var(--primary-color);
  font-weight: 600;
  margin-right: 8px;
}

.user-bio {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 20px 0;
  padding: 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  line-clamp: 3;
  -webkit-box-orient: vertical;
}

.user-contact {
  margin: 0 0 20px 0;
}

.user-contact p {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #888;
  font-size: 13px;
  margin: 0 0 8px 0;
}

/* 右侧信息修改区 */
.profile-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

.profile-section {
  background: var(--background-card);
  border-radius: 8px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid var(--border-color);
  overflow: hidden;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.profile-section h2 {
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  padding: 12px 20px;
  background: var(--background-secondary);
  border-bottom: 1px solid var(--border-color);
}

.profile-form {
  padding: 15px 20px;
  flex: 1;
  overflow-y: auto;
}

.form-group {
  margin-bottom: 15px;
  position: relative;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.form-group label {
  display: block;
  color: #606266;
  font-weight: 500;
  font-size: 14px;
  width: 80px;
  flex-shrink: 0;
  text-align: right;
}

.form-group input,
.form-group textarea,
.form-group select {
  flex: 1;
  min-width: 0;
  padding: 10px 14px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  color: #606266;
  font-size: 14px;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.form-group textarea {
  max-height: 80px;
  resize: none;
  min-height: 80px;
  align-self: flex-start;
  margin-top: 6px;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #ff6b96;
  box-shadow: 0 0 0 2px rgba(255, 107, 150, 0.1);
}

.form-group input:disabled,
.form-group textarea:disabled,
.form-group select:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.required {
  color: #ef4444;
  font-weight: bold;
}

.char-count {
  position: absolute;
  right: 14px;
  bottom: 10px;
  font-size: 0.8rem;
  color: #999;
  background: #f5f5f5;
  padding: 2px 6px;
  border-radius: 4px;
}

/* 处理多行输入的字符计数器位置 */
.form-group:has(textarea) .char-count {
  right: 14px;
  bottom: 14px;
}

/* 用户名检测状态样式 */
.input-with-status {
  position: relative;
  flex: 1;
  min-width: 0;
}

.status-indicator {
  margin-top: 6px;
  font-size: 0.9rem;
  font-weight: 500;
}

.status-indicator .checking {
  color: #f59e0b;
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-indicator .available {
  color: #10b981;
  display: flex;
  align-items: center;
  gap: 3px;
}

.status-indicator .unavailable {
  color: #ef4444;
  display: flex;
  align-items: center;
  gap: 3px;
}

.input-checking {
  border-color: #f59e0b !important;
  background: rgba(245, 158, 11, 0.05);
}

.input-available {
  border-color: #10b981 !important;
  background: rgba(16, 185, 129, 0.05);
}

.input-unavailable {
  border-color: #ef4444 !important;
  background: rgba(239, 68, 68, 0.05);
}

.form-group select {
  cursor: pointer;
}

.upload-btn {
  padding: 10px 25px;
  background: #ff6b96;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 3px 8px rgba(255, 107, 150, 0.3);
  margin-top: 10px;
}

.upload-btn:hover {
  background: #ff5283;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 150, 0.4);
}

.profile-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: auto;
  padding: 15px 20px 0 0;
}

.save-btn,
.cancel-btn {
  padding: 10px 24px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.save-btn {
  background: #409eff;
  color: #fff;
}

.save-btn:hover:not(:disabled) {
  background: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
}

.save-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
  transform: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.cancel-btn {
  background: #606266;
  color: #fff;
}

.cancel-btn:hover:not(:disabled) {
  background: #83878f;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(96, 98, 102, 0.3);
}

.cancel-btn:disabled {
  background: #c0c4cc;
  cursor: not-allowed;
  transform: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.readonly-input {
  background-color: #f5f5f5 !important;
  color: #666 !important;
  cursor: not-allowed !important;
}

.form-help {
  font-size: 0.7rem;
  color: #999;
  margin-top: 4px;
  display: block;
  margin-left: calc(80px + 12px);
}

/* 响应式设计 */
@media (max-width: 992px) {
  .profile-content {
    flex-direction: column;
    gap: 15px;
  }
  
  .profile-sidebar {
    width: 100%;
  }
  
  .user-info-card {
    max-width: 300px;
    margin: 0 auto;
    padding: 15px;
  }
  
  .user-avatar {
    width: 100px;
    height: 100px;
  }
}

@media (max-width: 768px) {
  .profile-page {
    padding: 10px;
  }
  
  .profile-container {
    padding: 15px;
    max-height: 95vh;
  }
  
  .profile-form {
    padding: 15px;
  }
  
  .profile-actions {
    justify-content: center;
    padding-right: 0;
  }
  
  .profile-header {
    margin-bottom: 15px;
    padding-bottom: 10px;
  }
}

@media (max-width: 480px) {
  .profile-page {
    padding: 5px;
  }
  
  .profile-container {
    padding: 10px;
  }
  
  .user-avatar {
    width: 80px;
    height: 80px;
    border-width: 2px;
  }
  
  .user-name {
    font-size: 16px;
  }
  
  .profile-content {
    gap: 10px;
  }
}

</style>