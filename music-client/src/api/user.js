import { request } from '@/utils/httpUtils.js'

export const register = (data) => request.post('/api/user/register', data)
export const login = (data) => request.post('/api/user/login', data)
export const getUserInfo = (userId) => request.get('/api/user/info', { params: { userId } })
export const changePassword = (userId, oldPassword, newPassword) => 
  request.post('/api/user/change-password', {}, { params: { userId, oldPassword, newPassword } })

// 获取用户个人资料
export const getUserProfile = (userId) => request.get('/api/user/profile', { params: { userId } })

// 更新用户个人资料
export const updateUserProfile = (userId, profileData) => 
  request.put('/api/user/profile', profileData, { params: { userId } })

// 更新用户头像（文件上传）
export const updateUserAvatar = (formData) => 
  request.upload('/api/files/upload', formData)

// 检查用户名是否可用
export const checkUsernameAvailability = (username, currentUserId = null) => {
  const params = { username }
  if (currentUserId) {
    params.currentUserId = currentUserId
  }
  return request.get('/api/user/check-username', { params })
}