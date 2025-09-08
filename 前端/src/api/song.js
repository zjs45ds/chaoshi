import { request } from '@/utils/httpUtils.js'

// 获取歌曲列表
export const getSongList = (page = 0, size = 10) => 
  request.get('/api/songs/list', { page, size })

// 根据ID获取歌曲详情
export const getSongById = (id) => 
  request.get(`/api/songs/${id}`)

// 收藏歌曲
export const favoriteSong = (userId, songId, action) => 
  request.post('/api/songs/favorite', {}, { params: { userId, songId, action } })

// 分页获取歌曲列表
export const getSongPage = (params = {}) => 
  request.get('/api/songs', params)

// 根据歌手ID获取歌曲
export const getSongsByArtist = (artistId) => 
  request.get(`/api/songs/artist/${artistId}`)

// 根据专辑ID获取歌曲
export const getSongsByAlbum = (albumId) => 
  request.get(`/api/songs/album/${albumId}`)

// 搜索歌曲
export const searchSongs = (keyword, page = 1, size = 10) => 
  request.get('/api/songs/search', { keyword, page, size })

// 创建歌曲
export const createSong = (formData) => 
  request.upload('/api/songs', formData)

// 更新歌曲
export const updateSong = (id, formData) => 
  request.upload(`/api/songs/${id}`, formData, { method: 'PUT' })

// 删除歌曲
export const deleteSong = (id) => 
  request.delete(`/api/songs/${id}`)

// 批量删除歌曲
export const deleteSongs = (ids) => 
  request.post('/api/songs/batch', ids) 

// 获取用户喜欢的歌曲列表
export const getUserFavoriteSongs = (userId, page = 1, size = 50) => 
  request.get(`/api/songs/users/${userId}/favorites`, { page, size })

// 检查歌曲是否被用户喜欢
export const isSongFavorited = (userId, songId) => 
  request.get(`/api/songs/${songId}/favorite-status`, { params: { userId } })

// 获取歌曲流媒体URL
export const getSongStreamUrl = (songId) => 
  request.get(`/api/songs/${songId}/stream-url`) 