import { request } from '@/utils/httpUtils.js'

/**
 * 获取首页轮播图
 * @returns {Promise<Array>} 轮播图列表
 */
export const getBanners = () => 
  request.get('/api/home/banners')

/**
 * 获取推荐歌单
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 推荐歌单列表
 */
export const getRecommendPlaylists = (limit = 10) => 
  request.get('/api/home/recommend/playlists', { limit })

/**
 * 获取热门歌手
 * @param {number} limit - 限制数量，默认为30
 * @returns {Promise<Array>} 热门歌手列表
 */
export const getHotArtists = (limit = 30) => 
  request.get('/api/home/hot/artists', { limit })

/**
 * 获取热门专辑
 * @param {number} limit - 限制数量，默认为30
 * @returns {Promise<Array>} 热门专辑列表
 */
export const getHotAlbums = (limit = 30) => 
  request.get('/api/home/hot/albums', { limit })

/**
 * 获取热门歌曲
 * @param {number} limit - 限制数量，默认为30
 * @returns {Promise<Array>} 热门歌曲列表
 */
export const getHotSongs = (limit = 30) => 
  request.get('/api/home/hot/songs', { limit })

/**
 * 获取热门排行榜
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 热门排行榜列表
 */
export const getHotToplists = (limit = 10) => 
  request.get('/api/home/hot/toplists', { limit })

/**
 * 获取热门MV
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 热门MV列表
 */
export const getHotMvs = (limit = 10) => 
  request.get('/api/home/hot/mvs', { limit })

/**
 * 获取首页推荐内容
 * @returns {Promise<Object>} 包含各种推荐内容的对象
 */
export const getHomeRecommends = () => 
  request.get('/api/home/recommends')

/**
 * 获取排行榜歌曲列表
 * @param {number} toplistId - 排行榜ID
 * @param {number} limit - 限制数量，默认为10
 * @returns {Promise<Array>} 排行榜歌曲列表
 */
export const getToplistSongs = (toplistId, limit = 10) => 
  request.get(`/api/home/toplist/${toplistId}/songs`, { limit })
