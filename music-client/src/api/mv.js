import { request } from '@/utils/httpUtils.js'

/**
 * 获取MV列表
 * @param {number} page - 页码，默认为0
 * @param {number} size - 每页数量，默认为10
 * @returns {Promise<Object>} 包含MV列表的响应对象
 */
export const getMVList = (page = 1, size = 10) => request.get('/api/mvs', { page, size })

/**
 * 获取MV详情
 * @param {number} id - MV ID
 * @returns {Promise<Object>} MV详情对象
 */
export const getMVById = (id) => request.get(`/api/mvs/${id}`)

/**
 * 获取歌手的MV列表
 * @param {number} artistId - 歌手ID
 * @param {number} page - 页码，默认为0
 * @param {number} size - 每页数量，默认为10
 * @returns {Promise<Object>} 包含歌手MV列表的响应对象
 */
export const getMVsByArtistId = (artistId, page = 1, size = 10) => request.get(`/api/mvs/artist/${artistId}`, { page, size })

/**
 * 获取推荐MV
 * @returns {Promise<Array>} 
 */
export const getRecommendedMVs = () => request.get('/api/mvs/recommend')

/**
 * 收藏MV
 * @param {number} mvId - MV ID
 * @param {string} action - 操作：'like' 或 'unlike'
 * @returns {Promise<Object>} 收藏结果
 */
export const favoriteMV = (userId, mvId, action) => request.post('/api/mvs/favorite', {}, { params: { userId, mvId, action } })

/**
 * 获取MV播放地址
 * @param {number} id - MV ID
 * @returns {Promise<Object>} 包含播放地址的响应对象
 */
export const getMVUrl = (id) => request.get(`/api/mvs/${id}/url`)