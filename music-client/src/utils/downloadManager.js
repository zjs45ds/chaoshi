/**
 * 下载管理器
 * 提供音乐文件下载功能，支持单个和批量下载
 */

import { ElMessage } from 'element-plus'

/**
 * 检查歌曲是否可下载
 * @param {Object} song 歌曲对象
 * @returns {boolean} 是否可下载
 */
export const isSongDownloadable = (song) => {
  if (!song) return false
  
  // 检查是否有有效的音频URL
  const audioUrl = song.audioUrl || song.filePath || song.file_path || ''
  
  // 排除无效URL和外部链接
  if (!audioUrl || 
      audioUrl.includes('spotify.com') || 
      audioUrl.includes('apple.com') ||
      audioUrl.includes('youtube.com') ||
      audioUrl.includes('netease.com') ||
      audioUrl.includes('qq.com') ||
      audioUrl === '' ||
      audioUrl === 'undefined' ||
      audioUrl === 'null') {
    return false
  }
  
  return true
}

/**
 * 获取可下载的歌曲列表
 * @param {Array} songs 歌曲列表
 * @returns {Array} 可下载的歌曲列表
 */
export const getDownloadableSongs = (songs) => {
  if (!Array.isArray(songs)) return []
  return songs.filter(song => isSongDownloadable(song))
}

/**
 * 生成下载文件名
 * @param {Object} song 歌曲对象
 * @returns {string} 文件名
 */
export const generateFilename = (song) => {
  if (!song) return 'unknown.mp3'
  
  const artist = song.artist || song.artistName || '未知歌手'
  const name = song.name || song.title || '未知歌曲'
  
  // 清理文件名中的非法字符
  const cleanText = (text) => {
    return text.replace(/[<>:"/\\|?*]/g, '_')
             .replace(/\s+/g, ' ')
             .trim()
  }
  
  const cleanArtist = cleanText(artist)
  const cleanName = cleanText(name)
  
  return `${cleanArtist} - ${cleanName}.mp3`
}

/**
 * 下载单个文件
 * @param {string} url 文件URL
 * @param {string} filename 文件名
 * @param {Function} onProgress 进度回调
 * @returns {Promise<Object>} 下载结果
 */
export const downloadFile = async (url, filename, onProgress = null) => {
  try {
    // CONSOLE LOG REMOVED: console.log(`开始下载文件: ${filename}`)
    // CONSOLE LOG REMOVED: console.log(`文件URL: ${url}`)
    
    // 处理相对路径URL
    let downloadUrl = url
    if (url.startsWith('/')) {
      // 相对路径，添加后端服务器地址
      const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
      downloadUrl = `${baseURL}${url}`
    }
    
    const response = await fetch(downloadUrl)
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const contentLength = response.headers.get('content-length')
    const total = contentLength ? parseInt(contentLength, 10) : 0
    
    let loaded = 0
    const reader = response.body.getReader()
    const chunks = []
    
    while (true) {
      const { done, value } = await reader.read()
      
      if (done) break
      
      chunks.push(value)
      loaded += value.length
      
      if (onProgress && total > 0) {
        const progress = Math.round((loaded / total) * 100)
        onProgress(progress)
      }
    }
    
    // 合并所有数据块
    const blob = new Blob(chunks)
    
    // 创建下载链接
    const blobUrl = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = blobUrl
    link.download = filename
    link.style.display = 'none'
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    // 清理URL对象
    setTimeout(() => {
      URL.revokeObjectURL(blobUrl)
    }, 1000)

    return {
      success: true,
      filename: filename,
      size: loaded
    }
    
  } catch (error) {
    return {
      success: false,
      filename: filename,
      error: error.message || '下载失败'
    }
  }
}

/**
 * 批量下载歌曲
 * @param {Array} songs 歌曲列表
 * @param {Function} onTotalProgress 总体进度回调
 * @param {Function} onFileProgress 单个文件进度回调
 * @returns {Promise<Object>} 下载结果统计
 */
export const downloadSongs = async (songs, onTotalProgress = null, onFileProgress = null) => {
  if (!Array.isArray(songs) || songs.length === 0) {
    throw new Error('歌曲列表为空')
  }

  const results = {
    total: songs.length,
    success: 0,
    failed: 0,
    skipped: 0,
    details: []
  }
  
  // 过滤可下载的歌曲
  const downloadableSongs = songs.filter(song => isSongDownloadable(song))
  results.skipped = songs.length - downloadableSongs.length
  
  if (downloadableSongs.length === 0) {
    throw new Error('没有可下载的歌曲')
  }

  // 逐个下载歌曲
  for (let i = 0; i < downloadableSongs.length; i++) {
    const song = downloadableSongs[i]
    const filename = generateFilename(song)
    const audioUrl = song.audioUrl || song.filePath || song.file_path || ''
    
    // 通知开始下载当前文件
    if (onFileProgress) {
      onFileProgress(i, song, 'downloading', 0)
    }
    
    try {
      const result = await downloadFile(
        audioUrl, 
        filename,
        (progress) => {
          if (onFileProgress) {
            onFileProgress(i, song, 'downloading', progress)
          }
        }
      )
      
      if (result.success) {
        results.success++
        if (onFileProgress) {
          onFileProgress(i, song, 'completed', 100)
        }
      } else {
        results.failed++
        if (onFileProgress) {
          onFileProgress(i, song, 'failed', 0)
        }
      }
      
      results.details.push(result)
      
    } catch (error) {
      // CONSOLE LOG REMOVED: console.error(`下载歌曲失败: ${song.name}`, error)
      results.failed++
      results.details.push({
        success: false,
        filename: filename,
        error: error.message || '下载失败'
      })
      
      if (onFileProgress) {
        onFileProgress(i, song, 'failed', 0)
      }
    }
    
    // 更新总体进度
    if (onTotalProgress) {
      const totalProgress = Math.round(((i + 1) / downloadableSongs.length) * 100)
      onTotalProgress(totalProgress, results)
    }
    
    // 添加小延迟，避免过快的请求
    if (i < downloadableSongs.length - 1) {
      await new Promise(resolve => setTimeout(resolve, 200))
    }
  }
  return results
}

/**
 * 显示下载结果摘要
 * @param {Object} results 下载结果
 */
export const showDownloadSummary = (results) => {
  if (!results) return
  
  const { total, success, failed, skipped } = results
  
  if (success === total) {
    ElMessage.success(`🎉 所有歌曲下载完成！共 ${success} 首`)
  } else if (success > 0) {
    ElMessage.warning(`⚠️ 下载完成：成功 ${success} 首，失败 ${failed} 首${skipped > 0 ? `，跳过 ${skipped} 首` : ''}`)
  } else {
    ElMessage.error(`❌ 下载失败，没有歌曲下载成功`)
  }

  if (results.details && results.details.length > 0) {
    results.details.forEach((detail, index) => {
      if (detail.success) {

      } else {
      }
    })
  }
}

/**
 * 检查浏览器下载功能支持
 * @returns {boolean} 是否支持下载
 */
export const isDownloadSupported = () => {
  try {
    return !!(window.URL && window.URL.createObjectURL && document.createElement)
  } catch (error) {
    return false
  }
}

/**
 * 获取下载文件的预估大小
 * @param {string} url 文件URL
 * @returns {Promise<number>} 文件大小（字节）
 */
export const getFileSize = async (url) => {
  try {
    // 处理相对路径URL
    let checkUrl = url
    if (url.startsWith('/')) {
      const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
      checkUrl = `${baseURL}${url}`
    }
    
    const response = await fetch(checkUrl, { method: 'HEAD' })
    const contentLength = response.headers.get('content-length')
    return contentLength ? parseInt(contentLength, 10) : 0
  } catch (error) {
    // CONSOLE LOG REMOVED: console.warn('获取文件大小失败:', error)
    return 0
  }
}

export default {
  downloadFile,
  downloadSongs,
  isSongDownloadable,
  getDownloadableSongs,
  generateFilename,
  showDownloadSummary,
  isDownloadSupported,
  getFileSize
}