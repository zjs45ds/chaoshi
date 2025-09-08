import { request } from '@/utils/httpUtils.js'

/**
 * 获取专辑列表
 * @param {number} page - 页码，默认为1
 * @param {number} size - 每页数量，默认为10
 * @returns {Promise<Object>} 包含专辑列表的响应对象
 */
export const getAlbumList = (page = 1, size = 10) => 
  request.get('/api/albums', { page, size })

/**
 * 获取专辑详情
 * @param {number} id - 专辑ID
 * @returns {Promise<Object>} 专辑详情对象
 */
export const getAlbumById = (id) => 
  request.get(`/api/albums/${id}`)

/**
 * 获取歌手的专辑列表
 * @param {number} artistId - 歌手ID
 * @param {number} page - 页码，默认为1
 * @param {number} size - 每页数量，默认为10
 * @returns {Promise<Object>} 包含歌手专辑列表的响应对象
 */
export const getAlbumsByArtistId = (artistId, page = 1, size = 10) => 
  request.get(`/api/albums/artist/${artistId}`, { page, size })

/**
 * 创建专辑
 * @param {FormData} formData - 专辑数据（包含文件）
 * @returns {Promise<Object>} 创建后的专辑对象
 */
export const createAlbum = (formData) => 
  request.upload('/api/albums', formData)

/**
 * 更新专辑
 * @param {number} id - 专辑ID
 * @param {FormData} formData - 专辑数据（包含文件）
 * @returns {Promise<Object>} 更新后的专辑对象
 */
export const updateAlbum = (id, formData) => 
  request.upload(`/api/albums/${id}`, formData, { method: 'PUT' })

/**
 * 删除专辑
 * @param {number} id - 专辑ID
 * @returns {Promise<Object>} 删除结果
 */
export const deleteAlbum = (id) => 
  request.delete(`/api/albums/${id}`)

/**
 * 获取专辑中的歌曲列表
 * @param {number} albumId - 专辑ID
 * @returns {Promise<Array>} 歌曲列表
 */
export const getSongsByAlbumId = (albumId) => 
  request.get(`/api/songs/album/${albumId}`)

/**
 * 搜索专辑
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 搜索结果
 */
export const searchAlbums = (keyword, page = 1, size = 10) => 
  request.get('/api/albums/search', { keyword, page, size })

/**
 * 批量删除专辑
 * @param {Array} ids - 专辑ID列表
 * @returns {Promise<Object>} 删除结果
 */
export const deleteAlbums = (ids) => 
  request.post('/api/albums/batch', ids)