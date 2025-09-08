import { request } from '@/utils/httpUtils.js'

/**
 * 获取歌手列表
 * @param {number} page - 页码，默认为1
 * @param {number} size - 每页数量，默认为10
 * @returns {Promise<Object>} 包含歌手列表的响应对象
 */
export const getArtistList = (page = 1, size = 10) => 
  request.get('/api/artists', { page, size })

/**
 * 获取歌手详情
 * @param {number} id - 歌手ID
 * @returns {Promise<Object>} 歌手详情对象
 */
export const getArtistById = (id) => 
  request.get(`/api/artists/${id}`)

/**
 * 获取热门歌手
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 热门歌手列表
 */
export const getHotArtists = (limit = 10) => 
  request.get('/api/artists/hot', { limit })

/**
 * 搜索歌手
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 搜索结果
 */
export const searchArtists = (keyword, page = 1, size = 10) => 
  request.get('/api/artists/search', { keyword, page, size })

/**
 * 获取歌手的歌曲列表
 * @param {number} artistId - 歌手ID
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 歌曲列表
 */
export const getSongsByArtist = (artistId, page = 1, size = 10) => 
  request.get(`/api/songs/artist/${artistId}`, { page, size })

/**
 * 获取歌手的专辑列表
 * @param {number} artistId - 歌手ID
 * @param {number} page - 页码
 * @param {number} size - 每页数量
 * @returns {Promise<Object>} 专辑列表
 */
export const getAlbumsByArtist = (artistId, page = 1, size = 10) => 
  request.get(`/api/albums/artist/${artistId}`, { page, size })

/**
 * 获取薛之谦专辑数据
 * @returns {Promise<Array>} 专辑列表
 */
export const fetchAlbums = async () => {
  try {
    const response = await request.get('/api/artists/xuezhiqian/albums')
    return response.data
  } catch (error) {
    console.error('获取专辑数据失败:', error)
    throw error
  }
}

/**
 * 获取薛之谦职业生涯时间线
 * @returns {Promise<Array>} 时间线事件列表
 */
export const fetchTimeline = async () => {
  try {
    const response = await request.get('/api/artists/xuezhiqian/timeline')
    return response.data
  } catch (error) {
    console.error('获取时间线数据失败:', error)
    throw error
  }
}

/**
 * 获取薛之谦热门歌曲
 * @returns {Promise<Array>} 歌曲列表
 */
export const fetchSongs = async () => {
  try {
    const response = await request.get('/api/artists/xuezhiqian/hotsongs')
    return response.data
  } catch (error) {
    console.error('获取歌曲数据失败:', error)
    throw error
  }
}

/**
 * 获取实时数据统计
 * @returns {Promise<Object>} 统计数据对象
 */
export const fetchStats = async () => {
  try {
    const response = await request.get('/api/artists/xuezhiqian/stats')
    return response.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
    throw error
  }
}