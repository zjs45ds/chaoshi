<template>
  <div class="profile-page">
    <div class="profile-container">
      <div class="profile-header">
        <h1>个人资料</h1>
        <p>管理您的个人信息和账户设置</p>
      </div>
      
      <div class="profile-content" v-loading="loading">
        <div class="profile-section">
          <h2>基本信息</h2>
          <div class="profile-form">
            <div class="form-group">
              <label>昵称 <span class="required">*</span></label>
              <input 
                v-model="profile.nickname" 
                type="text" 
                placeholder="请输入昵称" 
                maxlength="20"
                :disabled="loading || saving"
              />
              <span class="char-count">{{ profile.nickname?.length || 0 }}/20</span>
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
              <label>性别</label>
              <select v-model="profile.gender" :disabled="loading || saving">
                <option value="">请选择性别</option>
                <option value="male">男</option>
                <option value="female">女</option>
                <option value="other">其他</option>
              </select>
            </div>
            <div class="form-group">
              <label>生日</label>
              <input 
                v-model="profile.birthday" 
                type="date" 
                :disabled="loading || saving"
              />
            </div>
            <div class="form-group">
              <label>所在地</label>
              <input 
                v-model="profile.location" 
                type="text" 
                placeholder="请输入所在地" 
                maxlength="50"
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
        
        <div class="profile-section">
          <h2>头像设置</h2>
          <div class="avatar-section">
            <div class="current-avatar">
              <img :src="profile.avatar" alt="当前头像" />
            </div>
            <button class="upload-btn" @click="uploadAvatar">更换头像</button>
            <input ref="avatarInput" type="file" accept="image/*" style="display:none" @change="onAvatarChange" />
          </div>
        </div>
        
        <div class="profile-actions">
          <button 
            class="save-btn" 
            @click="saveProfile"
            :disabled="loading || saving"
          >
            <span v-if="saving">保存中...</span>
            <span v-else>保存更改</span>
          </button>
          <button 
            class="cancel-btn" 
            @click="resetProfile"
            :disabled="loading || saving"
          >
            重置
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { ElMessage, ElLoading } from 'element-plus'
import { handleAvatarUpload, triggerFileSelect, FILE_TYPES } from '@/utils/fileUpload.js'
import { getUserProfile, updateUserProfile, updateUserAvatar } from '@/api/user.js'

const profile = reactive({
  id: null,
  nickname: '',
  email: '',
  phone: '',
  bio: '',
  avatar: '',
  gender: '',
  birthday: '',
  location: ''
})

const originalProfile = ref({})
const loading = ref(false)
const saving = ref(false)
const avatarInput = ref(null)

// 获取当前用户ID
const getCurrentUserId = () => {
  return localStorage.getItem('userId') || localStorage.getItem('currentUserId') || '6'
}

// 加载用户个人资料
const loadUserProfile = async () => {
  try {
    loading.value = true
    const userId = getCurrentUserId()
    
    console.log('🔄 正在加载用户个人资料，用户ID:', userId)
    
    const response = await getUserProfile(userId)
    
    if (response && response.code === 200 && response.data) {
      const userData = response.data
      
      // 更新profile数据
      Object.assign(profile, {
        id: userData.id || userId,
        nickname: userData.nickname || userData.username || '未设置昵称',
        email: userData.email || '',
        phone: userData.phone || '',
        bio: userData.bio || userData.signature || '这个人很懒，什么都没有留下...',
        avatar: userData.avatar || userData.avatarUrl || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100',
        gender: userData.gender || '',
        birthday: userData.birthday || '',
        location: userData.location || ''
      })
      
      // 保存原始数据用于重置
      originalProfile.value = { ...profile }
      
      console.log('✅ 用户个人资料加载成功:', profile)
      
    } else {
      console.warn('⚠️ 获取用户资料失败，使用默认数据')
      // 使用默认数据
      setDefaultProfile()
    }
    
  } catch (error) {
    console.error('❌ 加载用户个人资料失败:', error)
    ElMessage.error('加载个人资料失败，请刷新页面重试')
    // 使用默认数据
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
    nickname: localStorage.getItem('userNickname') || '苏黎世的从前',
    email: '',
    phone: '',
    bio: localStorage.getItem('userBio') || '热爱音乐，分享美好。每一首歌都是心灵的触动，每一个音符都是情感的流露。',
    avatar: localStorage.getItem('userAvatar') || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100',
    gender: '',
    birthday: '',
    location: ''
  })
  originalProfile.value = { ...profile }
}

function uploadAvatar() {
  triggerFileSelect(avatarInput, { accept: FILE_TYPES.AVATAR })
}

async function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  
  try {
    loading.value = true
    const dataURL = await handleAvatarUpload(file, async (url) => {
      profile.avatar = url
      
      // 同时更新到数据库
      try {
        const userId = getCurrentUserId()
        await updateUserAvatar(userId, url)
        
        // 更新本地存储
        localStorage.setItem('userAvatar', url)
        
        ElMessage.success('头像更新成功！')
      } catch (error) {
        console.error('头像保存到数据库失败:', error)
        ElMessage.warning('头像上传成功，但保存到服务器失败')
      }
    })
  } catch (error) {
    ElMessage.error(error.message || '头像上传失败')
  } finally {
    loading.value = false
  }
}

// 表单验证
const validateProfile = () => {
  if (!profile.nickname || profile.nickname.trim().length === 0) {
    ElMessage.error('昵称不能为空')
    return false
  }
  
  if (profile.nickname.length > 20) {
    ElMessage.error('昵称不能超过20个字符')
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
    
    console.log('🔄 正在保存用户个人资料...')
    
    const profileData = {
      nickname: profile.nickname.trim(),
      email: profile.email.trim(),
      phone: profile.phone.trim(),
      bio: profile.bio.trim(),
      gender: profile.gender,
      birthday: profile.birthday,
      location: profile.location.trim()
    }
    
    const response = await updateUserProfile(userId, profileData)
    
    if (response && response.code === 200) {
      // 更新本地存储
      localStorage.setItem('userNickname', profile.nickname)
      localStorage.setItem('userBio', profile.bio)
      localStorage.setItem('userAvatar', profile.avatar)
      
      // 更新原始数据
      originalProfile.value = { ...profile }
      
      // 触发全局事件，通知其他组件更新个人信息
      window.dispatchEvent(new CustomEvent('user-profile-changed', {
        detail: {
          nickname: profile.nickname,
          bio: profile.bio,
          avatar: profile.avatar
        }
      }))
      
      console.log('✅ 个人资料保存成功')
      ElMessage.success('个人资料保存成功！')
      
    } else {
      throw new Error(response?.message || '保存失败')
    }
    
  } catch (error) {
    console.error('❌ 保存个人资料失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

function resetProfile() {
  Object.assign(profile, originalProfile.value)
  ElMessage.info('已重置为原始数据')
}

// 页面挂载时加载个人资料
onMounted(() => {
  loadUserProfile()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: var(--background);
  padding: 120px 20px 40px 20px;
}

.profile-container {
  max-width: 800px;
  margin: 0 auto;
  background: var(--background-card);
  border-radius: 16px;
  padding: 40px;
  box-shadow: var(--shadow-lg);
}

.profile-header {
  text-align: center;
  margin-bottom: 40px;
}

.profile-header h1 {
  color: var(--text-primary);
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.profile-header p {
  color: var(--text-secondary);
  font-size: 1.1rem;
}

.profile-section {
  margin-bottom: 40px;
}

.profile-section h2 {
  color: var(--text-primary);
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 20px;
  border-bottom: 2px solid var(--primary);
  padding-bottom: 8px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  color: var(--text-primary);
  font-weight: 500;
  margin-bottom: 8px;
}

.form-group {
  position: relative;
}

.form-group input,
.form-group textarea,
.form-group select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid var(--border);
  border-radius: 8px;
  background: var(--background-light);
  color: var(--text-primary);
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary);
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
  right: 8px;
  bottom: 8px;
  font-size: 0.8rem;
  color: var(--text-tertiary);
  background: var(--background-light);
  padding: 2px 6px;
  border-radius: 4px;
}

.form-group select {
  cursor: pointer;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 20px;
}

.current-avatar img {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 3px solid var(--primary);
}

.upload-btn {
  padding: 12px 24px;
  background: #000;
  color: #fff;
  border: 1px solid #fff;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-btn:hover {
  background: #333;
  transform: translateY(-2px);
}

.profile-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-top: 40px;
}

.save-btn,
.cancel-btn {
  padding: 12px 32px;
  border: 1px solid #fff;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.save-btn {
  background: #000;
  color: #fff;
}

.save-btn:hover:not(:disabled) {
  background: #333;
  transform: translateY(-2px);
}

.save-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.cancel-btn {
  background: #000;
  color: #fff;
}

.cancel-btn:hover:not(:disabled) {
  background: #333;
  transform: translateY(-2px);
}

.cancel-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .profile-container {
    padding: 20px;
  }
  
  .avatar-section {
    flex-direction: column;
    text-align: center;
  }
  
  .profile-actions {
    flex-direction: column;
  }
}
</style>