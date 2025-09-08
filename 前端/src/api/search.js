import { request } from '@/utils/httpUtils.js'

// 全局搜索
export const search = (keyword, type, page = 0, size = 10) => 
  request.get('/search', { keyword, type, page, size })

// 搜索建议/自动补全
export const searchSuggest = (keyword) => 
  request.get('/search/suggest', { keyword })

// 热门搜索
export const getHotSearch = () => 
  request.get('/search/hot')

// 搜索历史
export const getSearchHistory = () => 
  request.get('/search/history')

// 保存搜索历史
export const saveSearchHistory = (keyword) => 
  request.post('/search/history', { keyword })

// 清空搜索历史
export const clearSearchHistory = () => 
  request.delete('/search/history')

// 综合搜索（包含所有类型）
export const searchAll = (keyword, page = 0, size = 10) => 
  request.get('/search/all', { keyword, page, size })

// 模糊搜索（支持拼音、首字母、部分匹配）
export const fuzzySearch = (keyword, type = 'all', page = 0, size = 10) => 
  request.get('/search/fuzzy', { keyword, type, page, size })

// 智能搜索建议（实时搜索建议）
export const smartSuggest = (keyword, limit = 10) => 
  request.get('/search/smart-suggest', { keyword, limit })

// 获取热门歌手
export const getHotArtists = (limit = 10) => 
  request.get('/search/hot-artists', { limit })

// 获取热门歌曲
export const getHotSongs = (limit = 10) => 
  request.get('/search/hot-songs', { limit }) 