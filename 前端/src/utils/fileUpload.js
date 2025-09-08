/**
 * 通用文件上传和处理工具
 * 消除项目中重复的文件上传逻辑
 * 支持音频播放和头像更新的完整流程
 */

import { uploadFile, uploadImage, uploadAudio } from '@/api/file.js'
import { uploadAvatar } from '@/api/user.js'
import { ElMessage } from 'element-plus'

/**
 * 通用文件读取器
 * @param {File} file - 文件对象
 * @param {string} readType - 读取类型: 'dataURL' | 'text' | 'arrayBuffer'
 * @returns {Promise<string|ArrayBuffer>} 文件内容
 */
export function readFile(file, readType = 'dataURL') {
  return new Promise((resolve, reject) => {
    if (!file) {
      reject(new Error('文件不能为空'))
      return
    }
    
    const reader = new FileReader()
    
    reader.onload = (event) => {
      resolve(event.target.result)
    }
    
    reader.onerror = (error) => {
      reject(error)
    }
    
    switch (readType) {
      case 'dataURL':
        reader.readAsDataURL(file)
        break
      case 'text':
        reader.readAsText(file)
        break
      case 'arrayBuffer':
        reader.readAsArrayBuffer(file)
        break
      default:
        reader.readAsDataURL(file)
    }
  })
}

/**
 * 图片文件处理器
 * @param {File} file - 图片文件
 * @param {Object} options - 配置选项
 * @param {string} options.storageKey - localStorage存储键名
 * @param {string} options.eventName - 触发的事件名称
 * @param {Function} options.onSuccess - 成功回调
 * @param {Function} options.onError - 错误回调
 * @returns {Promise<string>} 图片的DataURL
 */
export async function handleImageUpload(file, options = {}) {
  const {
    storageKey,
    eventName,
    onSuccess,
    onError
  } = options
  
  try {
    // 验证文件类型
    if (!file.type.startsWith('image/')) {
      throw new Error('请选择图片文件')
    }
    
    // 验证文件大小 (5MB限制)
    if (file.size > 5 * 1024 * 1024) {
      throw new Error('图片大小不能超过5MB')
    }
    
    // 读取文件
    const dataURL = await readFile(file, 'dataURL')
    
    // 存储到localStorage
    if (storageKey) {
      localStorage.setItem(storageKey, dataURL)
    }
    
    // 触发自定义事件
    if (eventName) {
      window.dispatchEvent(new CustomEvent(eventName, {
        detail: { url: dataURL, file }
      }))
    }
    
    // 执行成功回调
    if (onSuccess) {
      onSuccess(dataURL, file)
    }
    
    return dataURL
    
  } catch (error) {
    console.error('图片上传失败:', error)
    
    // 执行错误回调
    if (onError) {
      onError(error)
    }
    
    throw error
  }
}

/**
 * 头像上传处理器 - 支持MinIO后端存储
 * @param {File} file - 头像文件
 * @param {Object} options - 配置选项
 * @param {number} options.userId - 用户ID
 * @param {Function} options.onSuccess - 成功回调
 * @param {Function} options.onError - 错误回调
 * @param {Function} options.onProgress - 进度回调
 * @returns {Promise<string>} MinIO存储的头像URL
 */
export async function handleAvatarUploadToMinIO(file, options = {}) {
  const { userId, onSuccess, onError, onProgress } = options
  
  try {
    // 验证文件
    if (!file.type.startsWith('image/')) {
      throw new Error('请选择图片文件')
    }
    
    if (file.size > MAX_FILE_SIZES.AVATAR) {
      throw new Error('头像大小不能超过2MB')
    }
    
    // 如果有用户ID，直接调用用户头像上传API
    if (userId) {
      const response = await uploadAvatar(userId, file)
      if (response && response.code === 200) {
        const avatarUrl = response.data
        
        // 更新localStorage
        localStorage.setItem('userAvatar', avatarUrl)
        
        // 触发全局事件
        window.dispatchEvent(new CustomEvent('user-avatar-changed', {
          detail: { url: avatarUrl, userId, file }
        }))
        
        if (onSuccess) {
          onSuccess(avatarUrl, file)
        }
        
        return avatarUrl
      } else {
        throw new Error(response?.message || '头像上传失败')
      }
    } else {
      // 通用图片上传
      const response = await uploadImage(file)
      if (response && response.code === 200) {
        const imageUrl = response.data
        
        if (onSuccess) {
          onSuccess(imageUrl, file)
        }
        
        return imageUrl
      } else {
        throw new Error(response?.message || '图片上传失败')
      }
    }
  } catch (error) {
    console.error('头像上传失败:', error)
    
    if (onError) {
      onError(error)
    }
    
    ElMessage.error(error.message || '头像上传失败')
    throw error
  }
}

/**
 * 头像上传处理器 - 保持向后兼容
 * @param {File} file - 头像文件
 * @param {Function} callback - 成功回调函数
 * @returns {Promise<string>} 头像的DataURL或MinIO URL
 */
export async function handleAvatarUpload(file, callback, useMinIO = true) {
  if (useMinIO) {
    return handleAvatarUploadToMinIO(file, {
      onSuccess: callback
    })
  } else {
    return handleImageUpload(file, {
      storageKey: 'userAvatar',
      eventName: 'user-avatar-changed',
      onSuccess: callback
    })
  }
}

/**
 * 背景图片上传处理器
 * @param {File} file - 背景图片文件
 * @param {Function} callback - 成功回调函数
 * @returns {Promise<string>} 背景图片的DataURL
 */
export async function handleBackgroundUpload(file, callback) {
  return handleImageUpload(file, {
    storageKey: 'userBannerBg',
    eventName: 'background-changed',
    onSuccess: callback
  })
}

/**
 * 音频文件处理器 - 支持MinIO后端存储
 * @param {File} file - 音频文件
 * @param {Object} options - 配置选项
 * @param {Function} options.onSuccess - 成功回调
 * @param {Function} options.onError - 错误回调
 * @param {Function} options.onProgress - 进度回调
 * @param {boolean} options.useMinIO - 是否使用MinIO存储
 * @returns {Promise<string>} MinIO存储的音频URL或DataURL
 */
export async function handleAudioUpload(file, options = {}) {
  const { onSuccess, onError, onProgress, useMinIO = true } = options
  
  try {
    // 验证文件类型
    const allowedTypes = ['audio/mp3', 'audio/wav', 'audio/ogg', 'audio/m4a', 'audio/mpeg', 'audio/flac']
    if (!allowedTypes.includes(file.type)) {
      throw new Error('请选择支持的音频格式 (MP3, WAV, OGG, M4A, FLAC)')
    }
    
    // 验证文件大小
    if (file.size > MAX_FILE_SIZES.AUDIO) {
      throw new Error('音频文件大小不能超过50MB')
    }
    
    let result
    
    if (useMinIO) {
      // 上传到MinIO
      const response = await uploadAudio(file)
      if (response && response.code === 200) {
        result = response.data
        
        // 触发音频上传完成事件
        window.dispatchEvent(new CustomEvent('audio-uploaded', {
          detail: { 
            url: result, 
            file,
            name: file.name,
            size: file.size,
            type: file.type
          }
        }))
        
        ElMessage.success('音频上传成功')
      } else {
        throw new Error(response?.message || '音频上传失败')
      }
    } else {
      // 读取为DataURL
      result = await readFile(file, 'dataURL')
    }
    
    // 执行成功回调
    if (onSuccess) {
      onSuccess(result, file)
    }
    
    return result
    
  } catch (error) {
    console.error('音频上传失败:', error)
    
    ElMessage.error(error.message || '音频上传失败')
    
    // 执行错误回调
    if (onError) {
      onError(error)
    }
    
    throw error
  }
}

/**
 * 音频流URL获取器
 * @param {number} songId - 歌曲ID
 * @returns {Promise<Object>} 包含音频URL和元数据的对象
 */
export async function getAudioStreamUrl(songId) {
  try {
    const { getSongStreamUrl } = await import('@/api/song.js')
    const response = await getSongStreamUrl(songId)
    
    if (response && response.success) {
      return {
        success: true,
        audioUrl: response.data.audioUrl || response.data.streamUrl,
        metadata: {
          artist: response.data.artist,
          cover: response.data.cover,
          duration: response.data.duration,
          title: response.data.title || response.data.name
        }
      }
    } else {
      throw new Error(response?.message || '获取音频流URL失败')
    }
  } catch (error) {
    console.error('获取音频流URL失败:', error)
    return {
      success: false,
      error: error.message
    }
  }
}

/**
 * 文件选择器触发器
 * @param {Object} inputRef - input元素的ref
 * @param {Object} options - 配置选项
 * @param {string} options.accept - 接受的文件类型
 * @param {boolean} options.multiple - 是否允许多选
 */
export function triggerFileSelect(inputRef, options = {}) {
  if (!inputRef || !inputRef.value) {
    console.warn('无效的input引用')
    return
  }
  
  // 设置文件类型限制
  if (options.accept) {
    inputRef.value.accept = options.accept
  }
  
  // 设置多选
  if (options.multiple !== undefined) {
    inputRef.value.multiple = options.multiple
  }
  
  // 触发文件选择
  inputRef.value.click()
}

/**
 * 获取文件信息
 * @param {File} file - 文件对象
 * @returns {Object} 文件信息
 */
export function getFileInfo(file) {
  if (!file) return null
  
  return {
    name: file.name,
    size: file.size,
    type: file.type,
    lastModified: file.lastModified,
    lastModifiedDate: new Date(file.lastModified),
    sizeFormatted: formatFileSize(file.size)
  }
}

/**
 * 格式化文件大小
 * @param {number} bytes - 字节数
 * @returns {string} 格式化后的文件大小
 */
export function formatFileSize(bytes) {
  if (bytes === 0) return '0 B'
  
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 统一文件上传管理器
 * @param {File} file - 文件对象
 * @param {Object} options - 配置选项
 * @param {string} options.type - 文件类型 ('image', 'audio', 'avatar')
 * @param {number} options.userId - 用户ID（头像上传时需要）
 * @param {Function} options.onSuccess - 成功回调
 * @param {Function} options.onError - 错误回调
 * @param {Function} options.onProgress - 进度回调
 * @returns {Promise<string>} 文件URL
 */
export async function uploadFileUnified(file, options = {}) {
  const { type = 'image', userId, onSuccess, onError, onProgress } = options
  
  try {
    let result
    
    switch (type) {
      case 'avatar':
        result = await handleAvatarUploadToMinIO(file, {
          userId,
          onSuccess,
          onError,
          onProgress
        })
        break
      
      case 'audio':
        result = await handleAudioUpload(file, {
          useMinIO: true,
          onSuccess,
          onError,
          onProgress
        })
        break
      
      case 'image':
      default:
        const response = await uploadImage(file)
        if (response && response.code === 200) {
          result = response.data
          if (onSuccess) onSuccess(result, file)
        } else {
          throw new Error(response?.message || '文件上传失败')
        }
        break
    }
    
    return result
  } catch (error) {
    if (onError) onError(error)
    throw error
  }
}

/**
 * 预设的文件类型
 */
export const FILE_TYPES = {
  IMAGE: 'image/*',
  AUDIO: 'audio/*',
  VIDEO: 'video/*',
  DOCUMENT: '.pdf,.doc,.docx,.txt',
  AVATAR: 'image/jpeg,image/png,image/gif,image/webp',
  MUSIC: 'audio/mp3,audio/wav,audio/ogg,audio/m4a,audio/flac'
}

/**
 * 最大文件大小限制 (字节)
 */
export const MAX_FILE_SIZES = {
  IMAGE: 5 * 1024 * 1024,      // 5MB
  AUDIO: 50 * 1024 * 1024,     // 50MB
  VIDEO: 100 * 1024 * 1024,    // 100MB
  AVATAR: 2 * 1024 * 1024      // 2MB
}