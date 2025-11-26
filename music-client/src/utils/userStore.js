/**
 * 用户信息统一管理
 * 从数据库获取最新的用户信息，确保所有组件使用一致的数据
 */

import { ref, reactive } from 'vue'
import { getUserProfile, getUserInfo } from '@/api/user.js'

// 全局用户信息状态
const userInfo = reactive({
  id: null,
  username: '',
  email: '',
  phone: '',
  bio: '',
  avatar: '',
  backgroundUrl: ''
})

const isLoading = ref(false)
const isInitialized = ref(false)

/**
 * 获取当前用户ID
 */
export const getCurrentUserId = () => {
  return localStorage.getItem('userId') || localStorage.getItem('currentUserId') || '1'
}

/**
 * 从数据库获取最新用户信息
 */
export const fetchUserInfo = async (userId = null) => {
  try {
    isLoading.value = true
    const targetUserId = userId || getCurrentUserId()
    
    // CONSOLE LOG REMOVED: console.log('📡 从数据库获取用户信息，用户ID:', targetUserId)
    
    let response = null
    let userData = null
    
    // 首先尝试使用profile API
    try {
      response = await getUserProfile(targetUserId)
      // CONSOLE LOG REMOVED: console.log('📡 Profile API响应:', response)
      
      if (response && response.code === 200 && response.data) {
        userData = response.data
        // CONSOLE LOG REMOVED: console.log('✅ 通过Profile API获取用户数据成功')
      }
    } catch (profileError) {
      // CONSOLE LOG REMOVED: console.warn('⚠️ Profile API调用失败，尝试备用API:', profileError)
      
      // 使用info API作为备用
      try {
        response = await getUserInfo(targetUserId)
        // CONSOLE LOG REMOVED: console.log('📡 Info API响应:', response)
        
        if (response && response.code === 200 && response.data) {
          userData = response.data
          // CONSOLE LOG REMOVED: console.log('✅ 通过Info API获取用户数据成功')
        }
      } catch (infoError) {
        // CONSOLE LOG REMOVED: console.error('❌ Info API也失败了:', infoError)
        throw new Error('无法获取用户信息')
      }
    }
    
    if (userData) {
      // 更新全局用户信息
      Object.assign(userInfo, {
        id: userData.id || targetUserId,
        username: userData.username || '用户',
        email: userData.email || '',
        phone: userData.phone || '',
        bio: userData.bio || '',
        avatar: userData.avatar || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100',
        backgroundUrl: userData.backgroundUrl || ''
      })
      
      // 同步更新localStorage（保持兼容性）
      localStorage.setItem('userNickname', userInfo.username)
      localStorage.setItem('nickname', userInfo.username)
      localStorage.setItem('username', userInfo.username)
      localStorage.setItem('userAvatar', userInfo.avatar)
      localStorage.setItem('userBio', userInfo.bio)
      if (userInfo.backgroundUrl) {
        localStorage.setItem('userBannerBg', userInfo.backgroundUrl)
      } else {
        localStorage.removeItem('userBannerBg')
      }
      
      isInitialized.value = true
      // CONSOLE LOG REMOVED: console.log('✅ 用户信息更新成功:', userInfo)
      
      // 触发全局事件通知其他组件
      window.dispatchEvent(new CustomEvent('user-info-updated', {
        detail: { userInfo: { ...userInfo } }
      }))
      window.dispatchEvent(new CustomEvent('background-changed', {
        detail: { url: userInfo.backgroundUrl || '' }
      }))
      
      return { ...userInfo }
    } else {
      throw new Error('未获取到用户数据')
    }
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('❌ 获取用户信息失败:', error)
    
    // 如果API失败，使用localStorage的备用数据
    const fallbackData = {
      id: getCurrentUserId(),
      username: localStorage.getItem('userNickname') || localStorage.getItem('nickname') || '用户',
      email: localStorage.getItem('userEmail') || '',
      phone: localStorage.getItem('userPhone') || '',
      bio: localStorage.getItem('userBio') || '',
      avatar: localStorage.getItem('userAvatar') || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100',
      backgroundUrl: localStorage.getItem('userBannerBg') || ''
    }
    
    Object.assign(userInfo, fallbackData)
    // CONSOLE LOG REMOVED: console.log('⚠️ 使用备用数据:', userInfo)
    
    return { ...userInfo }
  } finally {
    isLoading.value = false
  }
}

/**
 * 更新用户信息（用于Profile.vue保存后）
 */
export const updateUserInfo = (newUserInfo) => {
  Object.assign(userInfo, newUserInfo)
  
  // 同步更新localStorage
  localStorage.setItem('userNickname', userInfo.username)
  localStorage.setItem('nickname', userInfo.username)
  localStorage.setItem('username', userInfo.username)
  localStorage.setItem('userAvatar', userInfo.avatar)
  localStorage.setItem('userBio', userInfo.bio)
  if (userInfo.backgroundUrl) {
    localStorage.setItem('userBannerBg', userInfo.backgroundUrl)
  } else {
    localStorage.removeItem('userBannerBg')
  }
  
  // 触发全局事件
  window.dispatchEvent(new CustomEvent('user-info-updated', {
    detail: { userInfo: { ...userInfo } }
  }))
  
  // CONSOLE LOG REMOVED: console.log('✅ 用户信息已更新:', userInfo)
}

/**
 * 获取当前用户信息（响应式）
 */
export const getCurrentUserInfo = () => {
  return userInfo
}

/**
 * 获取用户名
 */
export const getUsername = () => {
  return userInfo.username || localStorage.getItem('userNickname') || localStorage.getItem('nickname') || '用户'
}

/**
 * 获取用户头像
 */
export const getUserAvatar = () => {
  return userInfo.avatar || localStorage.getItem('userAvatar') || 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100'
}

/**
 * 获取用户简介
 */
export const getUserBio = () => {
  return userInfo.bio || localStorage.getItem('userBio') || ''
}

/**
 * 检查是否已初始化
 */
export const getIsInitialized = () => {
  return isInitialized.value
}

/**
 * 检查是否正在加载
 */
export const getIsLoading = () => {
  return isLoading.value
}

/**
 * 初始化用户信息（应用启动时调用）
 */
export const initUserInfo = async () => {
  if (!isInitialized.value) {
    await fetchUserInfo()
  }
  return { ...userInfo }
}

/**
 * 清理用户信息（退出登录时调用）
 */
export const clearUserInfo = () => {
  // 清理全局用户信息状态
  Object.assign(userInfo, {
    id: null,
    username: '',
    email: '',
    phone: '',
    bio: '',
    avatar: '',
    backgroundUrl: ''
  })
  
  // 清理localStorage中的用户相关数据
  localStorage.removeItem('isLogin')
  localStorage.removeItem('userId')
  localStorage.removeItem('currentUserId')
  localStorage.removeItem('userNickname')
  localStorage.removeItem('nickname')
  localStorage.removeItem('username')
  localStorage.removeItem('userAvatar')
  localStorage.removeItem('userBio')
  localStorage.removeItem('userEmail')
  localStorage.removeItem('userPhone')
  localStorage.removeItem('userBannerBg')
  localStorage.removeItem('token')
  localStorage.removeItem('accessToken')
  
  // 清理sessionStorage中的用户相关数据
  sessionStorage.removeItem('isLogin')
  sessionStorage.removeItem('userId')
  sessionStorage.removeItem('currentUserId')
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('accessToken')
  
  // 重置状态
  isInitialized.value = false
  isLoading.value = false
  
  // 触发全局事件通知其他组件
  window.dispatchEvent(new CustomEvent('user-logout', {
    detail: { cleared: true }
  }))
  window.dispatchEvent(new CustomEvent('user-info-updated', {
    detail: { userInfo: { ...userInfo } }
  }))
  window.dispatchEvent(new CustomEvent('background-changed', {
    detail: { url: '' }
  }))
  
  console.log('✅ 用户信息已清理')
}

// 导出响应式状态供组件使用
export { userInfo, isLoading, isInitialized }