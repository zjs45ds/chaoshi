import { request } from '@/utils/httpUtils.js'

/**
 * 获取榜单列表
 * @returns {Promise<Array>} 榜单列表
 */
export const getToplistList = () => 
  request.get('/api/toplists/all')

/**
 * 获取榜单详情
 * @param {number} id - 榜单ID
 * @returns {Promise<Object>} 榜单详情对象
 */
export const getToplistById = (id) => 
  request.get(`/api/toplists/${id}`)

/**
 * 获取榜单歌曲
 * @param {number} toplistId - 榜单ID
 * @param {number} page - 页码，默认为1
 * @param {number} size - 每页数量，默认为50
 * @returns {Promise<Object>} 榜单歌曲列表
 */
export const getToplistSongs = (toplistId, page = 1, size = 50) => 
  request.get(`/api/toplists/${toplistId}/songs`, { page, size })

/**
 * 获取热门榜单
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 热门榜单列表
 */
export const getHotToplists = (limit = 10) => 
  request.get('/api/toplists/hot', { limit })