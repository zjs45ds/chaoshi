// 排行榜详情页面
<template>
  <!-- 二级导航栏 -->
  <SubNav />
  
  <div class="page-container">
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>
    
    <div v-else-if="toplist" class="toplist-page">
      <!-- 页面头部 -->
      <header class="page-header">
        <div class="header-content">
          <div class="toplist-info">
            <img :src="toplist.cover || 'https://picsum.photos/300/300?random=102'" 
                 :alt="toplist.name" 
                 class="toplist-cover" />
            <div class="info-text">
              <span class="category">排行榜</span>
              <h1 class="title">{{ toplist.name }}</h1>
              <p class="description">{{ toplist.description || '精选华语流行音乐' }}</p>
              <div class="meta">
                <span>{{ songs.length }} 首歌曲</span>
                <span>•</span>
                <span>更新于 2025-07-29</span>
              </div>
            </div>
          </div>
          <div class="header-actions">
            <button class="play-btn" @click="playAll">
              <svg viewBox="0 0 24 24" width="20" height="20">
                <path d="M8 5v14l11-7z" fill="currentColor"/>
              </svg>
              播放全部
            </button>
          </div>
        </div>
      </header>

      <!-- 歌曲列表 -->
      <main class="songs-content">
        <div class="songs-list" @click="closeMoreMenu">
          <div v-for="(song, index) in songs" :key="song.id || index" class="song-row" @click="playCurrentSong(song)">
            <div class="rank">{{ index + 1 }}</div>
            <div class="song-cover-cell">
              <img :src="song.cover || song.albumCover || 'https://picsum.photos/60/60?random=' + (index + 10)" 
                   :alt="song.name" 
                   class="song-cover" />
            </div>
            <div class="song-info-cell">
              <div class="song-combined-info">
                <span class="song-title clickable-item" @click="goToSongDetail(song, $event)">{{ song.name || '未知歌曲' }}</span>
                <span v-if="song.vip || song.isVip" class="vip-badge">VIP</span>
                <span class="song-artist clickable-item" @click="goToArtistDetail(song, $event)">{{ song.artist || song.artistName || '未知歌手' }}</span>
              </div>
            </div>
            <div class="song-duration">{{ formatDuration(song.duration) || '04:' + String(Math.floor(Math.random() * 60)).padStart(2, '0') }}</div>
            <div class="song-actions">
              <button class="btn-play" title="播放" @click.stop="playCurrentSong(song)">
                <svg viewBox="0 0 24 24" width="16" height="16">
                  <path d="M8 5v14l11-7z" fill="currentColor"/>
                </svg>
              </button>
              <div class="more-actions" @click.stop>
                <button class="btn-more" @click="toggleMoreMenu(index)" :title="'更多操作'">
                  <svg viewBox="0 0 24 24" width="16" height="16">
                    <path d="M12 8c1.1 0 2-.9 2-2s-.9-2-2-2-2 .9-2 2 .9 2 2 2zm0 2c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm0 6c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2z" fill="currentColor"/>
                  </svg>
                </button>
                <div v-if="activeMoreMenu === index" class="more-menu">
                  <div class="more-menu-item" @click="addToPlayNextAction(song)">
                    <svg viewBox="0 0 24 24" width="16" height="16">
                      <path d="M6 4h2v12H6V4zm3.5 6l8.5 6V4l-8.5 6z" fill="currentColor"/>
                    </svg>
                    <span>下一首播放</span>
                  </div>
                  <div class="more-menu-item" @click="addToFavorites(song)">
                    <svg viewBox="0 0 24 24" width="16" height="16">
                      <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" fill="currentColor"/>
                    </svg>
                    <span>收藏</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 空状态 -->
          <div v-if="songs.length === 0" class="empty-state">
            <div class="empty-icon">♪</div>
            <p>暂无歌曲</p>
          </div>
        </div>
      </main>
    </div>
    
    <div v-else class="error-message">
      <i class="el-icon-warning"></i>
      <span>排行榜不存在或已被删除</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getToplistById, getToplistSongs } from '@/api/toplist.js'
import { ElMessage } from 'element-plus'
import SubNav from '@/components/SubNav.vue'
import { playSong, addToPlaylist, addMultipleToPlaylist, addToPlayNext, playlist, playByIndex, currentIndex, isPlaying, isLoading, currentSong } from '@/utils/musicPlayer.js'

const route = useRoute()
const router = useRouter()
const toplist = ref(null)
const loading = ref(true)
const songs = ref([])
const activeMoreMenu = ref(null)

// 获取排行榜详情
const fetchToplistDetail = async () => {
  try {
    loading.value = true
    const toplistId = route.params.id
    
    // 并行获取排行榜详情和歌曲列表
    const [toplistResponse, songsResponse] = await Promise.allSettled([
      getToplistById(toplistId),
      getToplistSongs(toplistId, 1, 50)
    ])
    
    // 处理排行榜详情
    if (toplistResponse.status === 'fulfilled' && toplistResponse.value && toplistResponse.value.code === 200) {
      toplist.value = toplistResponse.value.data
      // CONSOLE LOG REMOVED: console.log('🏆 排行榜详情加载完成:', toplist.value)
    } else {
      ElMessage.error('排行榜不存在')
      return
    }
    
    // 处理排行榜歌曲列表
    if (songsResponse.status === 'fulfilled' && songsResponse.value && songsResponse.value.code === 200) {
      const songsData = songsResponse.value.data
      if (songsData && songsData.content && Array.isArray(songsData.content)) {
        songs.value = songsData.content
      } else if (songsData && Array.isArray(songsData)) {
        songs.value = songsData
      } else {
        songs.value = []
      }
      // CONSOLE LOG REMOVED: console.log('🎵 排行榜歌曲加载完成:', songs.value.length, '首歌曲')
    } else {
      // 如果没有歌曲数据，使用排行榜详情中的歌曲（如果存在）
      if (toplist.value && toplist.value.songs && Array.isArray(toplist.value.songs)) {
        songs.value = toplist.value.songs
        // CONSOLE LOG REMOVED: console.log('🎵 使用排行榜详情中的歌曲:', songs.value.length, '首')
      } else {
        songs.value = []
        // CONSOLE LOG REMOVED: console.warn('⚠️ 未获取到排行榜歌曲数据')
      }
    }
    
    // 确保toplist对象包含歌曲列表
    if (toplist.value) {
      toplist.value.songs = songs.value
    }
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('获取排行榜详情失败:', error)
    ElMessage.error('获取排行榜详情失败')
  } finally {
    loading.value = false
  }
}

const formatDuration = (duration) => {
  if (!duration) return '03:45'
  if (typeof duration === 'string') {
    // 如果已经是格式化的字符串，直接返回
    if (duration.includes(':')) return duration
    // 如果是秒数字符串，转换为数字处理
    duration = parseInt(duration)
  }
  if (typeof duration === 'number') {
    const minutes = Math.floor(duration / 60)
    const seconds = duration % 60
    return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  }
  return '03:45'
}

// 跳转到歌曲详情页
const goToSongDetail = (song, event) => {
  event.stopPropagation()
  if (song.id) {
    // CONSOLE LOG REMOVED: console.log('🎵 跳转到歌曲详情页:', song.name, 'ID:', song.id)
    router.push(`/song/${song.id}`)
  } else {
    // CONSOLE LOG REMOVED: console.warn('⚠️ 歌曲ID不存在，无法跳转')
    ElMessage.warning('歌曲信息不完整，无法查看详情')
  }
}

// 跳转到歌手详情页
const goToArtistDetail = (song, event) => {
  event.stopPropagation()
  // 尝试获取歌手ID，可能在不同字段
  const artistId = song.artistId || song.artist_id || song.singerId || song.singer_id
  
  if (artistId) {
    // CONSOLE LOG REMOVED: console.log('👤 跳转到歌手详情页:', song.artist || song.artistName, 'ID:', artistId)
    router.push(`/artist/${artistId}`)
  } else {
    // 如果没有歌手ID，可能是后端数据不完整，可以尝试提示用户
    // CONSOLE LOG REMOVED: console.warn('⚠️ 歌手ID不存在，无法跳转')
    ElMessage.warning('歌手信息不完整，无法查看详情')
  }
}

// 获取排名样式类
const getRankClass = (rank) => {
  if (rank <= 3) return 'rank-top'
  if (rank <= 10) return 'rank-high'
  return 'rank-normal'
}

// 获取趋势样式类
const getTrendClass = (index) => {
  return index % 2 === 0 ? 'trend-up' : 'trend-down'
}

// 播放单首歌曲 - 优先播放逻辑
const playCurrentSong = async (song) => {
  try {
    // CONSOLE LOG REMOVED: console.log('🎵 准备播放单首歌曲:', song.name)
    // CONSOLE LOG REMOVED: console.log('🎯 当前播放列表长度:', playlist.value.length)
    // CONSOLE LOG REMOVED: console.log('🎯 当前播放索引:', currentIndex.value)
    // CONSOLE LOG REMOVED: console.log('🎯 当前播放状态: isPlaying=', isPlaying.value, 'isLoading=', isLoading.value, 'currentSong=', currentSong.value?.name)
    
    // 检查歌曲是否已在播放列表中
    const existingIndex = playlist.value.findIndex(item => item.id === song.id)
    
    if (existingIndex !== -1) {
      // 如果歌曲已在列表中，将其移动到第一位并播放
      // CONSOLE LOG REMOVED: console.log('🔄 歌曲已在播放列表中，移动到第一位:', song.name)
      
      // 移除原位置的歌曲
      const targetSong = playlist.value.splice(existingIndex, 1)[0]
      // 插入到第一位
      playlist.value.unshift(targetSong)
      
      // 播放第一位的歌曲
      // CONSOLE LOG REMOVED: console.log('🎯 调用playByIndex(0)')
      const result = await playByIndex(0)
      // CONSOLE LOG REMOVED: console.log('✅ playByIndex结果:', result)
      if (result) {
        ElMessage.success(`开始播放: ${song.name}`)
      }
    } else {
      // 如果歌曲不在列表中，只添加这一首歌曲到第一位并播放
      // CONSOLE LOG REMOVED: console.log('📥 添加单首歌曲到播放列表第一位:', song.name)
      playlist.value.unshift(song)
      // CONSOLE LOG REMOVED: console.log('🎯 调用playByIndex(0)')
      const result = await playByIndex(0)
      // CONSOLE LOG REMOVED: console.log('✅ playByIndex结果:', result)
      if (result) {
        ElMessage.success(`开始播放: ${song.name}`)
      }
    }
    
    // CONSOLE LOG REMOVED: console.log('📋 操作后播放列表长度:', playlist.value.length)
    // CONSOLE LOG REMOVED: console.log('🎯 操作后播放索引:', currentIndex.value)
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放歌曲失败:', error)
    // CONSOLE LOG REMOVED: console.error('🚨 错误详情:', error.message)
    // CONSOLE LOG REMOVED: console.error('🚨 错误堆栈:', error.stack)
    ElMessage.error('播放失败，请稍后重试')
  }
}

// 切换更多菜单
const toggleMoreMenu = (index) => {
  if (activeMoreMenu.value === index) {
    activeMoreMenu.value = null
  } else {
    activeMoreMenu.value = index
  }
}

// 点击其他地方关闭菜单
const closeMoreMenu = () => {
  activeMoreMenu.value = null
}

// 添加到下一首播放
const addToPlayNextAction = async (song) => {
  try {
    // CONSOLE LOG REMOVED: console.log('⏭️ 添加到下一首播放:', song.name)
    
    const added = addToPlayNext(song)
    
    if (added) {
      // CONSOLE LOG REMOVED: console.log('✅ 歌曲已添加到下一首播放:', song.name)
      ElMessage.success(`${song.name} 已添加到下一首播放`)
    } else {
      ElMessage.error('添加失败')
    }
    
    // CONSOLE LOG REMOVED: console.log('📋 当前播放列表长度:', playlist.value.length)
    closeMoreMenu()
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('添加到下一首播放失败:', error)
    ElMessage.error('添加失败，请稍后重试')
  }
}

// 添加到收藏
const addToFavorites = async (song) => {
  try {
    // CONSOLE LOG REMOVED: console.log('❤️ 添加到收藏:', song.name)
    ElMessage.success(`${song.name} 已添加到收藏`)
    closeMoreMenu()
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('添加到收藏失败:', error)
    ElMessage.error('添加到收藏失败，请稍后重试')
  }
}


// 确保排行榜歌曲都在播放列表中
const ensureToplistInPlaylist = async () => {
  // 检查排行榜歌曲是否都在播放列表中
  const missingSongs = songs.value.filter(song => 
    !playlist.value.some(playlistSong => playlistSong.id === song.id)
  )
  
  if (missingSongs.length > 0) {
    // CONSOLE LOG REMOVED: console.log('📥 添加缺失的排行榜歌曲到播放列表:', missingSongs.length, '首')
    // 将缺失的歌曲添加到播放列表末尾，保持排行榜顺序
    playlist.value.push(...missingSongs)
  }
}

// 播放全部
const playAll = async () => {
  try {
    if (songs.value.length === 0) {
      ElMessage.warning('该排行榜暂无歌曲')
      return
    }
    
    // CONSOLE LOG REMOVED: console.log('🎵 准备播放排行榜:', toplist.value.name, songs.value.length, '首歌曲')
    
    // 使用 ensureToplistInPlaylist 确保所有歌曲都在播放列表中
    await ensureToplistInPlaylist()
    
    // 播放第一首歌曲（排行榜第一名）
    const firstSong = songs.value[0]
    if (firstSong) {
      const firstSongIndex = playlist.value.findIndex(item => item.id === firstSong.id)
      if (firstSongIndex !== -1) {
        playByIndex(firstSongIndex)
        ElMessage.success(`开始播放排行榜《${toplist.value.name}》`)
      }
    }
    
    // CONSOLE LOG REMOVED: console.log('✅ 排行榜歌曲已添加到播放列表')
    // CONSOLE LOG REMOVED: console.log('📋 当前播放列表长度:', playlist.value.length)
    
  } catch (error) {
    // CONSOLE LOG REMOVED: console.error('播放排行榜失败:', error)
    ElMessage.error('播放失败，请稍后重试')
  }
}

onMounted(() => {
  fetchToplistDetail()
})
</script>

<style scoped>
/* 全局容器 */
.page-container {
  min-height: 100vh;
  background: var(--background);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 20px;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 3px solid #e5e7eb;
  border-top: 3px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #6b7280;
  font-size: 16px;
  margin: 0;
}

/* 错误状态 */
.error-message {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  gap: 16px;
  color: #6b7280;
}

/* 主页面 */

/* 可点击元素视觉效果 */
.clickable-item {
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  display: inline-block;
}

.clickable-item:hover {
  color: #1890ff;
  transform: translateX(2px);
}

.clickable-item:active {
  transform: scale(0.95);
}

.song-title.clickable-item {
  font-weight: 500;
}

.song-title.clickable-item:hover {
  text-decoration: underline;
  text-underline-offset: 3px;
  text-decoration-thickness: 2px;
  text-decoration-color: #1890ff;
}

.song-artist.clickable-item:hover {
  background: linear-gradient(90deg, #1890ff, #722ed1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* 歌曲行悬停效果增强 */
.song-row:hover {
  background: rgba(24, 144, 255, 0.08);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.song-row {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}
.toplist-page {
  max-width: 1200px;
  margin: 0 auto;
  background: var(--background-card);
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  margin-top: 20px;
  margin-bottom: 40px;
}

/* 页面头部 */
.page-header {
  padding: 40px;
  background: var(--background-light);
  border-bottom: 1px solid var(--border);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 40px;
}

.toplist-info {
  display: flex;
  align-items: center;
  gap: 24px;
  flex: 1;
}

.toplist-cover {
  width: 160px;
  height: 160px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  flex-shrink: 0;
}

.info-text {
  flex: 1;
}

.category {
  display: inline-block;
  background: var(--primary);
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 12px;
}

.title {
  font-size: 32px;
  font-weight: 700;
  color: #1e293b;
  margin: 0 0 8px 0;
  line-height: 1.2;
}

.description {
  font-size: 16px;
  color: #64748b;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #64748b;
}

.meta span {
  display: flex;
  align-items: center;
}

/* 头部操作按钮 */
.header-actions {
  display: flex !important;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
  min-height: 50px;
  position: relative;
}

.play-btn {
  display: flex !important;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  z-index: 10;
  white-space: nowrap;
}

.play-btn:hover {
  background: var(--primary-dark);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: var(--background-light);
  color: #64748b;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.action-btn:hover {
  background: var(--background-hover);
  color: var(--text-primary);
  border-color: var(--border-hover);
}

/* 黑色主题样式 */
[data-theme="black"] .page-container {
  background: var(--background);
  color: var(--text-primary);
}

[data-theme="black"] .toplist-page {
  background: var(--background-card);
  box-shadow: 0 1px 3px rgba(255, 255, 255, 0.1);
}

[data-theme="black"] .page-header {
  background: var(--background-light);
  border-bottom: 1px solid var(--border);
}

[data-theme="black"] .title {
  color: var(--text-primary);
}

[data-theme="black"] .description {
  color: var(--text-secondary);
}

[data-theme="black"] .meta {
  color: var(--text-tertiary);
}

[data-theme="black"] .play-btn {
  background: #1e40af;
  color: white;
  border: none;
}

[data-theme="black"] .play-btn:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

[data-theme="black"] .song-row {
  background: transparent;
  color: var(--text-primary);
  border-bottom: 1px solid var(--border);
}

[data-theme="black"] .song-row:hover {
  background: var(--background-light);
}

[data-theme="black"] .song-name {
  color: var(--text-primary);
}

[data-theme="black"] .song-artist {
  color: var(--text-secondary);
}

[data-theme="black"] .song-album {
  color: var(--text-tertiary);
}

[data-theme="black"] .song-duration {
  color: var(--text-tertiary);
}

[data-theme="black"] .rank {
  color: var(--text-primary);
}

[data-theme="black"] .btn-play {
  background: #1e40af;
  color: white;
}

[data-theme="black"] .btn-play:hover {
  background: #1d4ed8;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

[data-theme="black"] .btn-more {
  background: var(--background-light);
  color: var(--text-secondary);
  border: none;
}

[data-theme="black"] .btn-more:hover {
  background: var(--background-card);
  color: var(--text-primary);
}

[data-theme="black"] .more-menu {
  background: var(--background-light);
  border: 1px solid var(--border);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

[data-theme="black"] .more-menu-item {
  color: var(--text-primary);
}

[data-theme="black"] .more-menu-item:hover {
  background: var(--background-card);
  color: var(--text-primary);
}

[data-theme="black"] .empty-state {
  background: var(--background-light);
  border: 2px dashed var(--border);
  color: var(--text-secondary);
}

/* 黑色主题下的收藏按钮样式 */
[data-theme="black"] .action-btn {
  background: #1e40af;
  color: white;
  border: 1px solid #3b82f6;
}

[data-theme="black"] .action-btn:hover {
  background: #1d4ed8;
  color: white;
  border-color: #60a5fa;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

/* 歌曲列表 */
.songs-content {
  padding: 0;
}

.songs-list {
  display: flex;
  flex-direction: column;
}

.song-row {
  display: grid;
  grid-template-columns: 60px 80px 1fr 120px 100px;
  gap: 24px;
  align-items: center;
  padding: 16px 40px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.15s ease;
}

.song-row:hover {
  background: var(--background-light);
}

.song-row:hover .song-actions {
  opacity: 1;
}

/* 排名 */
.rank {
  font-size: 16px;
  font-weight: 600;
  color: #64748b;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 歌曲封面单元格 */
.song-cover-cell {
  display: flex;
  align-items: center;
  justify-content: center;
}

.song-cover {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
  flex-shrink: 0;
}

/* 歌曲信息单元格 */
.song-info-cell {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  min-width: 0;
  overflow: hidden;
}

.song-combined-info {
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
}

.song-title {
  font-size: 15px;
  font-weight: 500;
  color: #1e293b;
  flex-shrink: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

.vip-badge {
  background: #ef4444;
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  flex-shrink: 0;
}

.song-artist {
  font-size: 15px;
  color: #64748b;
  flex-shrink: 1;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 时长 */
.song-duration {
  font-size: 14px;
  color: #64748b;
  text-align: center;
  font-variant-numeric: tabular-nums;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}

/* 操作按钮 */
.song-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.15s ease;
}

.btn-play {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3);
}

.btn-play:hover {
  background: var(--primary-dark);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.4);
}

/* 更多操作 */
.more-actions {
  position: relative;
}

.btn-more {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  background: #f1f5f9;
  color: #64748b;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-more:hover {
  background: #e2e8f0;
  color: #475569;
}

.more-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: var(--background-card);
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border: 1px solid #e2e8f0;
  z-index: 50;
  min-width: 160px;
  padding: 4px;
  margin-top: 4px;
}

.more-menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-size: 14px;
  color: #374151;
}

.more-menu-item:hover {
  background: #f3f4f6;
  color: #111827;
}

.more-menu-item svg {
  flex-shrink: 0;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 40px;
  text-align: center;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  margin: 40px;
  border: 2px dashed #cbd5e1;
  min-height: 200px;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
  color: #3b82f6;
  opacity: 0.8;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 0.8; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.05); }
}

.empty-state p {
  font-size: 18px;
  margin: 0;
  color: #475569;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .toplist-page {
    margin: 0;
    border-radius: 0;
  }
  
  .page-header {
    padding: 24px 20px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 24px;
  }
  
  .toplist-info {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .toplist-cover {
    width: 120px;
    height: 120px;
  }
  
  .title {
    font-size: 24px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: center;
  }
  
  .play-btn {
    min-width: 120px;
  }
  
  .song-row {
    grid-template-columns: 40px 50px 1fr 70px;
    padding: 16px 20px;
    gap: 16px;
  }
  
  .song-cover {
    width: 42px;
    height: 42px;
  }
  
  .rank {
    font-size: 14px;
  }
  
  .song-title {
    font-size: 14px;
  }
  
  .song-artist {
    font-size: 14px;
  }
  
  .song-actions {
    opacity: 1;
  }
  
  .btn-play {
    width: 32px;
    height: 32px;
  }
  
  .btn-more {
    width: 28px;
    height: 28px;
  }
  
  .song-duration {
    font-size: 12px;
  }
}
/* 黑色主题下的按钮样式 */
[data-theme="black"] .play-btn {
  background: #000000 !important;
  color: white !important;
  border: 1px solid white !important;
}

[data-theme="black"] .play-btn:hover {
  background: #1a1a1a !important;
  border-color: white !important;
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2) !important;
}

[data-theme="black"] .btn-play,
[data-theme="black"] .btn-more {
  background: #000000 !important;
  color: white !important;
  border: 1px solid white !important;
}

[data-theme="black"] .btn-play:hover,
[data-theme="black"] .btn-more:hover {
  background: #1a1a1a !important;
  border-color: white !important;
}

/* 黑色主题下的更多菜单中的收藏按钮样式 */
[data-theme="black"] .more-menu-item svg[viewBox="0 0 24 24"] path[d*="21.35"] {
  fill: #dc2626 !important; /* 红色爱心 */
}

</style>