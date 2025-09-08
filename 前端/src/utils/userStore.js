/**
 * 用户状态管理工具
 * 统一管理用户信息的存储、更新和事件通知
 */

// Vue 3 imports (for useUserProfile hook)
import { ref, onMounted, onUnmounted } from 'vue'

// 默认用户数据
const DEFAULT_USER_DATA = {
  avatar: 'https://q1.qlogo.cn/g?b=qq&nk=10000&s=100',
  nickname: '苏黎世的从前',
  bio: '热爱音乐，分享美好。每一首歌都是心灵的触动，每一个音符都是情感的流露。',
  bannerBg: 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=1920&h=1080&fit=crop&q=80'
}

// 用户数据存储键名
const STORAGE_KEYS = {
  AVATAR: 'userAvatar',
  NICKNAME: 'userNickname', 
  BIO: 'userBio',
  BANNER_BG: 'userBannerBg'
}

// 用户事件名称
const USER_EVENTS = {
  AVATAR_CHANGED: 'user-avatar-changed',
  NICKNAME_CHANGED: 'user-nickname-changed',
  BIO_CHANGED: 'user-bio-changed',
  BACKGROUND_CHANGED: 'background-changed',
  PROFILE_UPDATED: 'user-profile-updated'
}

/**
 * 获取用户数据
 * @param {string} key - 数据键名
 * @returns {string} 用户数据
 */
export function getUserData(key) {
  const storageKey = STORAGE_KEYS[key.toUpperCase()]
  const defaultValue = DEFAULT_USER_DATA[key]
  
  if (!storageKey) {
    console.warn(`未知的用户数据键: ${key}`)
    return defaultValue || ''
  }
  
  return localStorage.getItem(storageKey) || defaultValue
}

/**
 * 设置用户数据
 * @param {string} key - 数据键名
 * @param {string} value - 数据值
 * @param {boolean} triggerEvent - 是否触发更新事件
 */
export function setUserData(key, value, triggerEvent = true) {
  const storageKey = STORAGE_KEYS[key.toUpperCase()]
  const eventName = USER_EVENTS[`${key.toUpperCase()}_CHANGED`]
  
  if (!storageKey) {
    console.warn(`未知的用户数据键: ${key}`)
    return
  }
  
  // 存储到localStorage
  localStorage.setItem(storageKey, value)
  
  // 触发更新事件
  if (triggerEvent && eventName) {
    window.dispatchEvent(new CustomEvent(eventName, {
      detail: { key, value }
    }))
  }
}

/**
 * 获取完整的用户信息
 * @returns {Object} 用户信息对象
 */
export function getUserProfile() {
  return {
    avatar: getUserData('avatar'),
    nickname: getUserData('nickname'),
    bio: getUserData('bio'),
    bannerBg: getUserData('bannerBg')
  }
}

/**
 * 更新用户信息
 * @param {Object} userData - 用户数据对象
 * @param {boolean} triggerEvent - 是否触发更新事件
 */
export function updateUserProfile(userData, triggerEvent = true) {
  Object.keys(userData).forEach(key => {
    if (userData[key] !== undefined && userData[key] !== null) {
      setUserData(key, userData[key], false)
    }
  })
  
  // 统一触发个人资料更新事件
  if (triggerEvent) {
    window.dispatchEvent(new CustomEvent(USER_EVENTS.PROFILE_UPDATED, {
      detail: { userData }
    }))
  }
}

/**
 * 重置用户数据为默认值
 * @param {Array<string>} keys - 要重置的数据键名数组，不传则重置全部
 * @param {boolean} triggerEvent - 是否触发更新事件
 */
export function resetUserData(keys = null, triggerEvent = true) {
  const keysToReset = keys || Object.keys(DEFAULT_USER_DATA)
  
  keysToReset.forEach(key => {
    const defaultValue = DEFAULT_USER_DATA[key]
    if (defaultValue !== undefined) {
      setUserData(key, defaultValue, false)
    }
  })
  
  if (triggerEvent) {
    window.dispatchEvent(new CustomEvent(USER_EVENTS.PROFILE_UPDATED, {
      detail: { reset: true, keys: keysToReset }
    }))
  }
}

/**
 * 创建用户数据监听器
 * @param {Object} handlers - 事件处理器对象
 * @param {Function} handlers.onAvatarChanged - 头像变更处理器
 * @param {Function} handlers.onNicknameChanged - 昵称变更处理器  
 * @param {Function} handlers.onBioChanged - 个人简介变更处理器
 * @param {Function} handlers.onBackgroundChanged - 背景变更处理器
 * @param {Function} handlers.onProfileUpdated - 个人资料更新处理器
 * @returns {Function} 清理函数
 */
export function createUserDataListener(handlers = {}) {
  const {
    onAvatarChanged,
    onNicknameChanged,
    onBioChanged,
    onBackgroundChanged,
    onProfileUpdated
  } = handlers
  
  // 创建事件监听器
  const listeners = []
  
  if (onAvatarChanged) {
    const listener = () => onAvatarChanged(getUserData('avatar'))
    window.addEventListener(USER_EVENTS.AVATAR_CHANGED, listener)
    listeners.push([USER_EVENTS.AVATAR_CHANGED, listener])
  }
  
  if (onNicknameChanged) {
    const listener = () => onNicknameChanged(getUserData('nickname'))
    window.addEventListener(USER_EVENTS.NICKNAME_CHANGED, listener)
    listeners.push([USER_EVENTS.NICKNAME_CHANGED, listener])
  }
  
  if (onBioChanged) {
    const listener = () => onBioChanged(getUserData('bio'))
    window.addEventListener(USER_EVENTS.BIO_CHANGED, listener)
    listeners.push([USER_EVENTS.BIO_CHANGED, listener])
  }
  
  if (onBackgroundChanged) {
    const listener = (event) => onBackgroundChanged(event.detail?.value || getUserData('bannerBg'))
    window.addEventListener(USER_EVENTS.BACKGROUND_CHANGED, listener)
    listeners.push([USER_EVENTS.BACKGROUND_CHANGED, listener])
  }
  
  if (onProfileUpdated) {
    const listener = (event) => onProfileUpdated(getUserProfile(), event.detail)
    window.addEventListener(USER_EVENTS.PROFILE_UPDATED, listener)
    listeners.push([USER_EVENTS.PROFILE_UPDATED, listener])
  }
  
  // 返回清理函数
  return function cleanup() {
    listeners.forEach(([eventName, listener]) => {
      window.removeEventListener(eventName, listener)
    })
  }
}

/**
 * Vue 3 组合式API钩子
 * @param {Object} options - 配置选项
 * @returns {Object} 响应式用户数据和方法
 */
export function useUserProfile(options = {}) {
  const avatar = ref(getUserData('avatar'))
  const nickname = ref(getUserData('nickname'))
  const bio = ref(getUserData('bio'))
  const bannerBg = ref(getUserData('bannerBg'))
  
  let cleanup = null
  
  onMounted(() => {
    // 创建数据监听器
    cleanup = createUserDataListener({
      onAvatarChanged: (value) => { avatar.value = value },
      onNicknameChanged: (value) => { nickname.value = value },
      onBioChanged: (value) => { bio.value = value },
      onBackgroundChanged: (value) => { bannerBg.value = value },
      onProfileUpdated: (profile) => {
        avatar.value = profile.avatar
        nickname.value = profile.nickname
        bio.value = profile.bio
        bannerBg.value = profile.bannerBg
      }
    })
  })
  
  onUnmounted(() => {
    if (cleanup) {
      cleanup()
    }
  })
  
  return {
    avatar,
    nickname,
    bio,
    bannerBg,
    getUserData,
    setUserData,
    updateUserProfile,
    resetUserData
  }
}

// 导出常量
export {
  DEFAULT_USER_DATA,
  STORAGE_KEYS,
  USER_EVENTS
}