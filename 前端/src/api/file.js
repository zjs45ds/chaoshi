import { request } from '@/utils/httpUtils.js'

/**
 * 文件上传API模块
 */

// 上传文件到MinIO
export const uploadFile = (file, fileType) => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('fileType', fileType)
  
  return request.upload('/api/files/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      const progress = Math.round(
        (progressEvent.loaded * 100) / progressEvent.total
      )
      console.log(`Upload progress: ${progress}%`)
    }
  })
}

// 上传图片
export const uploadImage = (file) => {
  return uploadFile(file, 'image')
}

// 上传音频
export const uploadAudio = (file) => {
  return uploadFile(file, 'audio')
}

// 上传视频
export const uploadVideo = (file) => {
  return uploadFile(file, 'video')
}

// 删除文件
export const deleteFile = (fileUrl) => {
  return request.delete('/api/files/delete', {
    params: { fileUrl }
  })
}

// 获取文件预签名URL
export const getPresignedUrl = (objectName) => {
  return request.get('/api/files/presigned-url', {
    objectName
  })
}

// 批量上传文件
export const uploadMultipleFiles = async (files, fileType, onProgress) => {
  const promises = files.map((file, index) => {
    return uploadFile(file, fileType).then(url => {
      if (onProgress) {
        onProgress(index + 1, files.length)
      }
      return { file, url }
    })
  })
  
  return Promise.all(promises)
}

// 文件类型验证
export const validateFileType = (file, allowedTypes) => {
  const fileType = file.type
  return allowedTypes.some(type => fileType.startsWith(type))
}

// 文件大小验证
export const validateFileSize = (file, maxSize) => {
  return file.size <= maxSize
}

// 图片文件验证
export const validateImage = (file, maxSize = 10 * 1024 * 1024) => {
  return validateFileType(file, ['image/']) && validateFileSize(file, maxSize)
}

// 音频文件验证
export const validateAudio = (file, maxSize = 50 * 1024 * 1024) => {
  return validateFileType(file, ['audio/']) && validateFileSize(file, maxSize)
}

// 视频文件验证
export const validateVideo = (file, maxSize = 100 * 1024 * 1024) => {
  return validateFileType(file, ['video/']) && validateFileSize(file, maxSize)
}