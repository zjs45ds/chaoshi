/**
 * 播放列表功能测试
 * 用于验证播放列表管理器的基本功能
 */

import { 
  addToPlaylist, 
  addMultipleToPlaylist,
  playNext,
  playPrevious,
  togglePlayMode,
  clearPlaylist,
  playlist,
  currentIndex,
  playMode,
  isPlaying
} from './playlistManager.js'

// 测试歌曲数据
const testSongs = [
  {
    id: 'test_1',
    name: '测试歌曲1',
    artist: '测试歌手1',
    album: '测试专辑1',
    duration: 210,
    cover: '/test-cover-1.jpg',
    audioUrl: '/test-audio-1.mp3'
  },
  {
    id: 'test_2',
    name: '测试歌曲2',
    artist: '测试歌手2',
    album: '测试专辑2',
    duration: 185,
    cover: '/test-cover-2.jpg',
    audioUrl: '/test-audio-2.mp3'
  },
  {
    id: 'test_3',
    name: '测试歌曲3',
    artist: '测试歌手3',
    album: '测试专辑3',
    duration: 245,
    cover: '/test-cover-3.jpg',
    audioUrl: '/test-audio-3.mp3'
  }
]

/**
 * 运行基本功能测试
 */
export const runPlaylistTests = () => {
  console.log('🎵 开始播放列表功能测试...')
  
  try {
    // 清空播放列表
    clearPlaylist()
    console.log('✅ 清空播放列表成功')
    
    // 测试添加单个歌曲
    const result1 = addToPlaylist(testSongs[0], true)
    console.log(`✅ 添加单个歌曲${result1 ? '成功' : '失败'}`)
    console.log(`当前播放列表长度: ${playlist.value.length}`)
    console.log(`当前播放索引: ${currentIndex.value}`)
    console.log(`当前播放状态: ${isPlaying.value}`)
    
    // 测试批量添加歌曲
    const result2 = addMultipleToPlaylist(testSongs.slice(1))
    console.log(`✅ 批量添加歌曲${result2 ? '成功' : '失败'}`)
    console.log(`当前播放列表长度: ${playlist.value.length}`)
    
    // 测试播放模式切换
    const originalMode = playMode.value
    const newMode = togglePlayMode()
    console.log(`✅ 播放模式切换: ${originalMode} -> ${newMode}`)
    
    // 测试下一首
    const nextResult = playNext()
    console.log(`✅ 播放下一首${nextResult ? '成功' : '失败'}`)
    console.log(`当前播放索引: ${currentIndex.value}`)
    
    // 测试上一首
    const prevResult = playPrevious()
    console.log(`✅ 播放上一首${prevResult ? '成功' : '失败'}`)
    console.log(`当前播放索引: ${currentIndex.value}`)
    
    console.log('🎉 播放列表功能测试完成！')
    
    return {
      success: true,
      playlistLength: playlist.value.length,
      currentIndex: currentIndex.value,
      playMode: playMode.value,
      isPlaying: isPlaying.value
    }
    
  } catch (error) {
    console.error('❌ 播放列表功能测试失败:', error)
    return {
      success: false,
      error: error.message
    }
  }
}

/**
 * 在浏览器控制台中运行测试
 * 使用方法：在浏览器开发者工具中运行 window.testPlaylist()
 */
if (typeof window !== 'undefined') {
  window.testPlaylist = runPlaylistTests
}