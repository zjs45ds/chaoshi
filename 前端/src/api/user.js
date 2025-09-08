import { request } from '@/utils/httpUtils.js'

export const register = (data) => request.post('/api/user/register', data)
export const login = (data) => request.post('/api/user/login', data)
export const getUserInfo = (userId) => request.get('/api/user/info', { userId })
export const changePassword = (userId, oldPassword, newPassword) => 
  request.post('/api/user/change-password', {}, { params: { userId, oldPassword, newPassword } })
export const uploadAvatar = (userId, file) => {
  const formData = new FormData()
  formData.append('avatar', file)
  formData.append('userId', userId)
  return request.upload('/api/user/upload-avatar', formData)
}

// 获取用户个人资料
export const getUserProfile = (userId) => request.get('/api/user/profile', { userId })

// 更新用户个人资料
export const updateUserProfile = (userId, profileData) => 
  request.put('/api/user/profile', profileData, { params: { userId } })

// 更新用户头像
export const updateUserAvatar = (userId, avatarUrl) => 
  request.put('/api/user/avatar', { avatarUrl }, { params: { userId } }) 