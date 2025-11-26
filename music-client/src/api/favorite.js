import { request } from '@/utils/httpUtils.js'

/**
 * 收藏相关API
 */

// ============ 专辑收藏 ============

/**
 * 收藏/取消收藏专辑
 * @param {number} albumId - 专辑ID
 * @param {number} userId - 用户ID
 * @param {boolean} isFavorited - 当前是否已收藏（true=已收藏，需要取消；false=未收藏，需要收藏）
 */
export const favoriteAlbum = (albumId, userId, isFavorited) => {
  const action = isFavorited ? 'unlike' : 'like'
  return request.post(`/api/albums/${albumId}/favorite`, null, {
    params: { userId, action }
  })
}

/**
 * 检查专辑收藏状态
 * @param {number} albumId - 专辑ID
 * @param {number} userId - 用户ID
 */
export const getAlbumFavoriteStatus = (albumId, userId) => {
  return request.get(`/api/albums/${albumId}/favorite-status`, {
    params: { userId }
  })
}

/**
 * 获取用户收藏的专辑列表
 * @param {number} userId - 用户ID
 */
export const getUserFavoriteAlbums = (userId) => {
  return request.get('/api/albums/favorites', {
    params: { userId }
  })
}

// ============ MV收藏 ============

/**
 * 收藏/取消收藏MV
 * @param {number} mvId - MV ID
 * @param {number} userId - 用户ID
 * @param {boolean} isFavorited - 当前是否已收藏（true=已收藏，需要取消；false=未收藏，需要收藏）
 */
export const favoriteMv = (mvId, userId, isFavorited) => {
  const action = isFavorited ? 'unlike' : 'like'
  return request.post(`/api/mvs/${mvId}/favorite`, null, {
    params: { userId, action }
  })
}

/**
 * 检查MV收藏状态
 * @param {number} mvId - MV ID
 * @param {number} userId - 用户ID
 */
export const getMvFavoriteStatus = (mvId, userId) => {
  return request.get(`/api/mvs/${mvId}/favorite-status`, {
    params: { userId }
  })
}

/**
 * 获取用户收藏的MV列表
 * @param {number} userId - 用户ID
 */
export const getUserFavoriteMvs = (userId) => {
  return request.get('/api/mvs/favorites', {
    params: { userId }
  })
}

// ============ 歌曲收藏（已有功能） ============

/**
 * 收藏/取消收藏歌曲
 * @param {number} songId - 歌曲ID
 * @param {number} userId - 用户ID
 * @param {boolean} isFavorited - 当前是否已收藏（true=已收藏，需要取消；false=未收藏，需要收藏）
 */
export const favoriteSong = (songId, userId, isFavorited) => {
  const action = isFavorited ? 'unlike' : 'like'
  // 使用song.js中的API，参数顺序是(userId, songId, action)
  return request.post('/api/songs/favorite', {}, {
    params: { userId, songId, action }
  })
}

/**
 * 检查歌曲收藏状态
 * @param {number} songId - 歌曲ID
 * @param {number} userId - 用户ID
 */
export const getSongFavoriteStatus = (songId, userId) => {
  return request.get(`/api/songs/${songId}/favorite-status`, {
    params: { userId }
  })
}

/**
 * 获取用户收藏的歌曲列表
 * @param {number} userId - 用户ID
 */
export const getUserFavoriteSongs = (userId) => {
  return request.get('/api/songs/favorites', {
    params: { userId }
  })
}

// ============ 通用收藏操作 ============

/**
 * 切换收藏状态
 * @param {string} type - 收藏类型 'song' | 'album' | 'mv'
 * @param {number} itemId - 项目ID
 * @param {number} userId - 用户ID
 * @param {boolean} currentStatus - 当前收藏状态
 */
export const toggleFavorite = async (type, itemId, userId, currentStatus) => {
  switch (type) {
    case 'song':
      // favoriteSong参数顺序：(songId, userId, isFavorited)
      return await favoriteSong(itemId, userId, currentStatus)
    case 'album':
      return await favoriteAlbum(itemId, userId, currentStatus)
    case 'mv':
      return await favoriteMv(itemId, userId, currentStatus)
    default:
      throw new Error('不支持的收藏类型: ' + type)
  }
}

/**
 * 获取收藏状态
 * @param {string} type - 收藏类型 'song' | 'album' | 'mv'
 * @param {number} itemId - 项目ID
 * @param {number} userId - 用户ID
 */
export const getFavoriteStatus = async (type, itemId, userId) => {
  switch (type) {
    case 'song':
      return await getSongFavoriteStatus(itemId, userId)
    case 'album':
      return await getAlbumFavoriteStatus(itemId, userId)
    case 'mv':
      return await getMvFavoriteStatus(itemId, userId)
    default:
      throw new Error('不支持的收藏类型: ' + type)
  }
}

/**
 * 获取用户收藏列表
 * @param {string} type - 收藏类型 'song' | 'album' | 'mv'
 * @param {number} userId - 用户ID
 */
export const getUserFavorites = async (type, userId) => {
  switch (type) {
    case 'song':
      return await getUserFavoriteSongs(userId)
    case 'album':
      return await getUserFavoriteAlbums(userId)
    case 'mv':
      return await getUserFavoriteMvs(userId)
    default:
      throw new Error('不支持的收藏类型: ' + type)
  }
}
