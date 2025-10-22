import { request } from '@/utils/httpUtils.js'

// 全局搜索
export const search = (keyword, type, page = 0, size = 10) => 
  request.get('/search', { params: { keyword, type, page, size } })

// 搜索建议/自动补全
export const searchSuggest = (keyword) => 
  request.get('/search/suggest', { params: { keyword } })

// 热门搜索
export const getHotSearch = () => 
  request.get('/search/hot')

// 搜索历史
export const getSearchHistory = (userId = null) => {
  const params = userId ? { userId } : {}
  return request.get('/search/history', { params })
}

// 保存搜索历史
export const saveSearchHistory = (keyword, userId = null) => {
  const data = { keyword }
  if (userId) {
    data.userId = userId
  }
  return request.post('/search/history', data)
}

// 删除单个搜索历史
export const deleteSearchHistoryItem = (keyword, userId = null) => {
  const url = `/search/history/${encodeURIComponent(keyword)}${userId ? `?userId=${userId}` : ''}`
  return request.delete(url)
}

// 清空搜索历史
export const clearSearchHistory = (userId = null) => {
  const url = `/search/history${userId ? `?userId=${userId}` : ''}`
  return request.delete(url)
}

// 综合搜索（包含所有类型）
export const searchAll = (keyword, page = 0, size = 10, userId = null) => {
  const params = { keyword, page, size }
  if (userId) {
    params.userId = userId
  }
  return request.get('/search/all', { params })
}

// 模糊搜索（支持拼音、首字母、部分匹配）
export const fuzzySearch = (keyword, type = 'all', page = 0, size = 10) => 
  request.get('/search/fuzzy', { params: { keyword, type, page, size } })

// 智能搜索建议（实时搜索建议）
export const smartSuggest = (keyword, limit = 10) => 
  request.get('/search/smart-suggest', { params: { keyword, limit } })

// 获取热门歌手
export const getHotArtists = (limit = 10) => 
  request.get('/search/hot-artists', { params: { limit } })

// 获取热门歌曲
export const getHotSongs = (limit = 10) => 
  request.get('/search/hot-songs', { params: { limit } })