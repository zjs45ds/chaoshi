import { request } from '@/utils/httpUtils.js'

/**
 * 获取歌单列表
 * @param {number} page - 页码，默认为1
 * @param {number} size - 每页数量，默认为10
 * @returns {Promise<Object>} 包含歌单列表的响应对象
 */
export const getPlaylistList = (page = 1, size = 10) => 
  request.get('/api/playlists', { page, size })

/**
 * 获取歌单详情
 * @param {number} id - 歌单ID
 * @returns {Promise<Object>} 歌单详情对象
 */
export const getPlaylistById = (id) => 
  request.get(`/api/playlists/${id}`)

/**
 * 获取推荐歌单
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 推荐歌单列表
 */
export const getRecommendPlaylists = (limit = 10) => 
  request.get('/api/playlists/recommend', { limit })

/**
 * 创建歌单
 * @param {string} name - 歌单名称
 * @param {string} coverUrl - 封面图片URL
 * @param {string} description - 描述
 * @param {number} userId - 用户ID
 * @returns {Promise<Object>} 创建的歌单对象
 */
export const createPlaylist = (name, coverUrl, description = '', userId = 1) => {
  const formData = new FormData()
  formData.append('name', name)
  formData.append('coverUrl', coverUrl)
  formData.append('description', description)
  formData.append('userId', userId.toString())
  formData.append('isPublic', 'false')
  
  return request.post('/api/playlists', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 收藏歌单
 * @param {number} userId - 用户ID
 * @param {number} playlistId - 歌单ID
 * @param {string} action - 操作：'favorite' 或 'unfavorite'
 * @returns {Promise<Object>} 收藏结果
 */
export const favoritePlaylist = (userId, playlistId, action) => 
  request.post('/api/playlists/favorite', {}, { params: { userId, playlistId, action } })

/**
 * 获取用户的歌单
 * @param {number} userId - 用户ID
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 用户歌单列表
 */
export const getUserPlaylists = (userId, page = 1, size = 10) => 
  request.get(`/api/playlists/user/${userId}`, { page, size })

/**
 * 搜索歌单
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 搜索结果
 */
export const searchPlaylists = (keyword, page = 1, size = 10) => 
  request.get('/api/playlists/search', { keyword, page, size })

/**
 * 删除歌单
 * @param {number} id - 歌单ID
 * @returns {Promise<Object>} 删除结果
 */
export const deletePlaylist = (id) => 
  request.delete(`/api/playlists/${id}`) 