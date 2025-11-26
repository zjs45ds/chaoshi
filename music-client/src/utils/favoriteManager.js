/**
 * 喜欢歌曲状态管理工具
 * 提供全局的收藏歌曲状态管理功能
 */

import { ref, reactive } from 'vue'
import { favoriteSong, getUserFavoriteSongs, isSongFavorited } from '@/api/song.js'
import { ElMessage } from 'element-plus'

// 全局状态
const favoriteSongs = ref([])
const favoriteStatus = reactive(new Map()) // songId -> boolean

/**
 * 获取用户ID
 */
export const getUserId = () => {
  // 尝试从localStorage获取用户信息
  const userInfo = localStorage.getItem('userInfo')
  
  if (userInfo) {
    try {
      const user = JSON.parse(userInfo)
      return user.id || user.userId || null
    } catch (e) {
      console.error('解析用户信息失败:', e)
    }
  }
  
  // 尝试直接获取userId
  const userId = localStorage.getItem('userId')
  if (userId) {
    return userId
  }
  
  // 如果都没有，返回null表示未登录
  console.warn('用户未登录，无法获取用户ID')
  return null
}

/**
 * 初始化用户喜欢的歌曲列表
 */
export const initFavoriteSongs = async () => {
  try {
    const userId = getUserId()
    if (!userId) {
      // CONSOLE LOG REMOVED: console.log('用户未登录，无法获取喜欢的歌曲')
      return
    }

    // CONSOLE LOG REMOVED: console.log('正在获取用户喜欢的歌曲，用户ID:', userId)
    const response = await getUserFavoriteSongs(userId, 1, 50)
    
    if (response && response.code === 200 && response.data) {
      // 处理分页数据结构
      const songsData = response.data.content || response.data.data || response.data || []
      
      // 转换数据格式以兼容前端显示
      favoriteSongs.value = songsData.map(song => ({
        id: song.id,
        name: song.name,
        artist: song.artistName || song.artist || '未知歌手',
        album: song.albumName || song.album || '未知专辑',
        artistId: song.artistId || song.artist_id,
        albumId: song.albumId || song.album_id,
        duration: formatDuration(song.duration || 0),
        cover: song.coverUrl || song.cover || 'https://qpic.y.qq.com/music_cover/N6GhicG06jmQnia2FZRicpvhQXiaLPoEJcRlnjtIlFeBXTuPgsgdFwykWg/600?n=1',
        audioUrl: song.audioUrl || song.filePath || song.file_path || ''
      }))
      
      // 更新收藏状态映射
      favoriteStatus.clear()
      favoriteSongs.value.forEach(song => {
        favoriteStatus.set(song.id, true)
      })
      
      // CONSOLE LOG REMOVED: console.log('成功获取用户喜欢的歌曲:', favoriteSongs.value.length, '首')
    } else {
      // CONSOLE LOG REMOVED: console.warn('获取用户喜欢歌曲失败:', response?.message || '未知错误')
      favoriteSongs.value = []
    }
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('获取用户喜欢歌曲失败:', error)
    favoriteSongs.value = []
  }
}

/**
 * 检查歌曲是否被喜欢
 */
export const isSongLiked = (songId) => {
  return favoriteStatus.get(songId) || false
}

/**
 * 切换歌曲喜欢状态
 */
export const toggleSongLike = async (song) => {
  try {
    const userId = getUserId()
    if (!userId) {
      // CONSOLE LOG REMOVED: console.warn('用户未登录，无法执行收藏操作')
      return false
    }
    
    if (!song || !song.id) {
      // CONSOLE LOG REMOVED: console.warn('歌曲信息不完整')
      return false
    }
    
    const isCurrentlyLiked = isSongLiked(song.id)
    const action = isCurrentlyLiked ? 'unlike' : 'like'
    
    // 参数顺序修正：根据song.js中的定义，应该是(userId, songId, action)
    const response = await favoriteSong(userId, song.id, action)
    
    if (response && response.code === 200) {
      // 更新本地状态
      if (isCurrentlyLiked) {
        // 移除
        favoriteSongs.value = favoriteSongs.value.filter(s => s.id !== song.id)
        favoriteStatus.set(song.id, false)
        // CONSOLE LOG REMOVED: console.log(`♡ 已从我喜欢中移除：${song.name}`)
      } else {
        // 添加 - 确保歌曲对象格式完整，包含所有必要字段
        const formattedSong = {
          id: song.id,
          name: song.name,
          artist: song.artist || song.artistName || '未知歌手',
          album: song.album || song.albumName || '未知专辑',
          artistId: song.artistId || '',
          albumId: song.albumId || '',
          duration: formatDuration(song.duration || 0),
          cover: song.cover || song.coverUrl || 'https://qpic.y.qq.com/music_cover/N6GhicG06jmQnia2FZRicpvhQXiaLPoEJcRlnjtIlFeBXTuPgsgdFwykWg/600?n=1',
          audioUrl: song.audioUrl || song.filePath || ''
        }
        
        // 确保不添加重复歌曲
        if (!favoriteSongs.value.some(s => s.id === song.id)) {
          favoriteSongs.value.unshift(formattedSong)
        }
        favoriteStatus.set(song.id, true)
        // CONSOLE LOG REMOVED: console.log(`♥ 已添加到我喜欢：${song.name}`)
      }
      
      // 触发自定义事件，通知其他组件更新
      window.dispatchEvent(new CustomEvent('songLikeChanged', {
        detail: { 
          songId: song.id, 
          isLiked: !isCurrentlyLiked,
          song: song
        }
      }))
      
      // 只有在成功添加到我喜欢的列表后才返回true
      return !isCurrentlyLiked
    } else {
      // CONSOLE LOG REMOVED: console.error('收藏操作失败，请稍后重试')
      // 当操作失败时，重新同步后端状态
      await refreshFavoriteSongs()
      return isSongLiked(song.id)
    }
  } catch (error) {
    let shouldShowError = true
    let errorMessage = '操作失败'
    
    // 特殊处理：歌曲已在收藏夹中
    if (error.response?.data?.message && error.response.data.message.includes('歌曲已经在收藏夹中')) {
      // 静默处理这种情况，直接更新本地状态为已收藏
      // 使用格式化的歌曲对象，确保包含所有必要字段
      const formattedSong = {
        id: song.id,
        name: song.name,
        artist: song.artist || song.artistName || '未知歌手',
        album: song.album || song.albumName || '未知专辑',
        artistId: song.artistId || '',
        albumId: song.albumId || '',
        duration: formatDuration(song.duration || 0),
        cover: song.cover || song.coverUrl || 'https://qpic.y.qq.com/music_cover/N6GhicG06jmQnia2FZRicpvhQXiaLPoEJcRlnjtIlFeBXTuPgsgdFwykWg/600?n=1',
        audioUrl: song.audioUrl || song.filePath || ''
      }
      
      favoriteSongs.value = [formattedSong, ...favoriteSongs.value.filter(s => s.id !== song.id)]
      favoriteStatus.set(song.id, true)
      
      // 触发自定义事件，通知其他组件更新
      window.dispatchEvent(new CustomEvent('songLikeChanged', {
        detail: { 
          songId: song.id, 
          isLiked: true,
          song: formattedSong
        }
      }))
      
      shouldShowError = false
      // CONSOLE LOG REMOVED: console.log(`歌曲${song.name}已在收藏夹中，已更新本地状态`)
      return true
    } else if (error.message === 'Network Error' || error.code === 'ECONNABORTED' || error.code === 'ECONNREFUSED') {
      // 网络错误已在httpUtils.js中处理
      shouldShowError = false
    } else if (error.response?.data?.message) {
      errorMessage = error.response.data.message
    } else if (error.message) {
      errorMessage = error.message
    }
    
    if (shouldShowError) {
      // CONSOLE LOG REMOVED: console.error('收藏操作失败:', errorMessage)
    }
    
    // 无论如何，返回最新的本地状态
    return isSongLiked(song.id)
  }
}

/**
 * 获取喜欢的歌曲列表
 */
export const getFavoriteSongs = () => {
  return favoriteSongs.value
}

/**
 * 刷新喜欢的歌曲列表
 */
export const refreshFavoriteSongs = async () => {
  await initFavoriteSongs()
}

/**
 * 批量检查歌曲喜欢状态
 */
export const checkSongsLikeStatus = async (songs) => {
  try {
    const userId = getUserId()
    if (!userId || !songs || songs.length === 0) return
    
    // 批量检查可以优化为单个API调用，这里先用现有API
    const promises = songs.map(async (song) => {
      try {
        const response = await isSongFavorited(userId, song.id)
        const isFavorited = response?.data === true || response?.data?.isFavorited === true
        favoriteStatus.set(song.id, isFavorited)
        // CONSOLE LOG REMOVED: console.log(`歌曲 ${song.id} 收藏状态:`, isFavorited)
      } catch (error) {
        // CONSOLE LOG REMOVED: console.log(`检查歌曲${song.id}喜欢状态失败:`, error)
        favoriteStatus.set(song.id, false)
      }
    })
    
    await Promise.all(promises)
  } catch (error) {
    // CONSOLE LOG REMOVED: console.log('批量检查歌曲喜欢状态失败:', error)
  }
}

/**
 * 单独检查歌曲收藏状态
 */
export const checkSongLikeStatus = async (songId) => {
  try {
    const userId = getUserId()
    if (!userId || !songId) return false
    
    const response = await isSongFavorited(userId, songId)
    const isFavorited = response?.data === true || response?.data?.isFavorited === true
    favoriteStatus.set(songId, isFavorited)
    // CONSOLE LOG REMOVED: console.log(`检查歌曲 ${songId} 收藏状态:`, isFavorited)
    return isFavorited
  } catch (error) {
    // CONSOLE LOG REMOVED: console.log(`检查歌曲${songId}收藏状态失败:`, error)
    favoriteStatus.set(songId, false)
    return false
  }
}

/**
 * 格式化时长（秒转为mm:ss格式）
 */
const formatDuration = (seconds) => {
  if (!seconds || seconds === 0) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

// 导出状态供外部使用
export { favoriteSongs, favoriteStatus }